/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ghostwalker18.scheduledesktop;

import com.sun.istack.Nullable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;
import okhttp3.ResponseBody;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

/**
 * Этот класс представляет собой репозиторий данных приложения.
 *
 * @author  Ипатов Никита
 */
public class ScheduleRepository {
    private static ScheduleRepository repository = null;
    private  final Preferences preferences = Preferences.userNodeForPackage(ScheduleRepository.class);
    private final ScheduleNetworkAPI api;
    private final AppDatabase db;
    private final String baseUri = "https://ptgh.onego.ru/9006/";
    private final String mainSelector = "h2:contains(Расписание занятий и объявления:) + div > table > tbody";
    private final String mondayTimesPath = "mondayTimes.jpg";
    private final String otherTimesPath = "otherTimes.jpg";
    private final BehaviorSubject<BufferedImage> mondayTimes = BehaviorSubject.create();
    private final BehaviorSubject<BufferedImage> otherTimes = BehaviorSubject.create();
    private final PublishSubject<Status> status = PublishSubject.create();

    /**
     * Этот метод используется для получения доступа к репозиторию.
     *
     * @return синглтон репозитория приложения
     */
    public static ScheduleRepository getRepository(){
        if(repository == null)
                repository = new ScheduleRepository();
        return repository;
    }

    private ScheduleRepository(){
        db = AppDatabaseHibernate.getInstance();
        api = new Retrofit.Builder()
                .baseUrl(baseUri)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .build()
                .create(ScheduleNetworkAPI.class);
    }

    /**
     * Этот метод используется для получения настроек приложения.
     *
     * @return настройки приложения
     */
    public Preferences getPreferences(){
        return preferences;
    }

    /**
     * Этот метод используется для получения состояния,
     * в котором находится процесс обновления репозитория.
     *
     * @return статус состояния
     */
    public Observable<Status> getStatus(){
        return status;
    }

    /**
     * Этот метод используется для получения буфферизированого файла изображения
     * расписания звонков на понедельник.
     *
     * @return фото расписания звонков на понедельник
     */
    public Observable<BufferedImage> getMondayTimes(){
        return mondayTimes;
    }

    /**
     * Этот метод используется для получения буфферизированого файла изображения
     * расписания звонков со вторника по пятницу.
     *
     * @return фото расписания звонков со вторника по пятницу
     */
    public Observable<BufferedImage> getOtherTimes(){
        return otherTimes;
    }

    /**
     * Этот метод обновляет репозиторий приложения.
     * Метод использует многопоточность и может вызывать исключения в других потоках.
     * Требуется интернет соединение.
     */
    public void update(){
        //updating times files
        File mondayTimesFile = new File(mondayTimesPath);
        File otherTimesFile = new File(otherTimesPath);
        if(!preferences.getBoolean("doNotUpdateTimes", true) || !mondayTimesFile.exists() || !otherTimesFile.exists()){
            Call<ResponseBody> mondayTimesResponse = api.getMondayTimes();
            mondayTimesResponse.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.body() != null){
                        try{
                            BufferedImage image = ImageIO.read(response.body().byteStream());
                            mondayTimes.onNext(image);
                            ImageIO.write(image, "jpg", mondayTimesFile);
                        }
                        catch (Exception e){}
                        finally {
                            response.body().close();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {}
            });
            Call<ResponseBody> otherTimesResponse = api.getOtherTimes();
            otherTimesResponse.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.body() != null){
                        try{
                            BufferedImage image = ImageIO.read(response.body().byteStream());
                            otherTimes.onNext(image);
                            ImageIO.write(image, "jpg", otherTimesFile);
                        }
                        catch (Exception e){}
                        finally {
                            response.body().close();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {}
            });
        }
        else{
            new Thread(() -> {
                try{
                    BufferedImage bitmap1 = ImageIO.read(mondayTimesFile);
                    mondayTimes.onNext(bitmap1);
                    BufferedImage bitmap2 = ImageIO.read(otherTimesFile);
                    otherTimes.onNext(bitmap2);
                }
                catch(Exception e){}
            }).start();
        }
        new Thread(() -> {
            List<String> scheduleLinks = getLinksForScheduleFirstCorpus();
            if(scheduleLinks.size() == 0)
                status.onNext(new Status("Ошибка при скачивании", 0));
            for(String link : scheduleLinks){
                status.onNext(new Status("Скачивание расписания", 10));
                api.getScheduleFile(link).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.body() != null){
                            status.onNext(new Status("Обработка расписания", 33));
                            try(XSSFWorkbook excelFile = new XSSFWorkbook(response.body().byteStream())){
                                List<Lesson> lessons = XMLStoLessonsConverter.convertFirstCorpus(excelFile);
                                db.insertMany(lessons);
                                status.onNext(new Status("Расписание успешно обновлено", 100));
                            }
                            catch (IOException e){
                            }
                            response.body().close();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                });
            }
        }).start();
    }

    /**
     * Этот метод возвращает всех учителей, упоминаемых в расписании.
     *
     * @return список учителей
     */
    public Observable<List<String>> getTeachers(){
        return db.getTeachers();
    }

    /**
     * Этот метод возвращает все группы, упоминаемые в расписании.
     *
     * @return список групп
     */
    public Observable<List<String>> getGroups(){
        return db.getGroups();
    }

    /**
     * Этот метод возращает список занятий в этот день у группы у данного преподавателя.
     * Если группа не указана, то возвращается список занятий у преподавателя в этот день.
     * Если учитель не указан, то возвращается список занятй у группы в этот день.
     *
     * @param date день
     * @param teacher преподаватель
     * @param group группа
     * @return спискок занятий
     */
    public Observable<List<Lesson>> getSchedule(Calendar date, @Nullable String teacher, @Nullable String group){
        if (teacher != null && group != null)
            return db.getLessonsForGroupWithTeacher(date, group, teacher);
        else if (teacher != null)
            return db.getLessonsForTeacher(date, teacher);
        else if (group != null)
            return db.getLessonsForGroup(date, group);
        else return BehaviorSubject.createDefault(new LinkedList<>());
    }

    /**
     * Этот метод получает ссылки с сайта ПАСТ,
     * по которым доступно расписание для корпуса на Первомайском проспекте.
     *
     * @return список ссылок
     */
    public List<String> getLinksForScheduleFirstCorpus(){
        List<String> links = new ArrayList<>();
        try{
            Document doc = Jsoup.connect(baseUri).get();
            Elements linkElements = doc.select(mainSelector).get(0)
                    .select("tr").get(1)
                    .select("td").get(1)
                    .select("p > strong > span > a");
            for(Element linkElement : linkElements){
                links.add(linkElement.attr("href"));
            }
            return links;
        }
        catch (IOException e){
            return links;
        }
    }

    /**
     * Этот метод получает ссылки с сайта ПАСТ,
     * по которым доступно основное расписание для корпуса на Мурманской улице.
     *
     * @return список ссылок
     */
    public String getLinkForScheduleSecondCorpusMain(){
        try{
            Document doc = Jsoup.connect(baseUri).get();
            Element linkElement = doc.select(mainSelector).get(0)
                    .select("tr").get(1)
                    .select("td").get(0)
                    .select("p > a").get(0);
            return linkElement.attr("href");
        }
        catch (IOException e){
            return null;
        }
    }

    /**
     * Этот метод получает ссылки с сайта ПАСТ,
     * по которым доступны изменения расписания для корпуса на Мурманской улице.
     *
     * @return список ссылок
     */
    public List<String> getLinksForScheduleSecondCorpusAdditional(){
        List<String> links = new ArrayList<>();
        try{
            Document doc = Jsoup.connect(baseUri).get();
            Elements linkElements = doc.select(mainSelector).get(0)
                    .select("tr").get(1)
                    .select("td").get(0)
                    .select("p > strong > span > a");
            for(Element linkElement : linkElements){
                links.add(linkElement.attr("href"));
            }
            return links;
        }
        catch(IOException r){
            return links;
        }
    }

    /**
     * Этот класс используетс для отображения статуса обновления репозитория.
     */
    public static class Status{
        public String text;
        public int progress;

        public Status(String text, int progress){
            this.text = text;
            this.progress = progress;
        }
    }
}