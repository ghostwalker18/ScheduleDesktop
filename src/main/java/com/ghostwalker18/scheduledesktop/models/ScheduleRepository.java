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

package com.ghostwalker18.scheduledesktop.models;

import com.ghostwalker18.scheduledesktop.*;
import com.ghostwalker18.scheduledesktop.converters.IConverter;
import com.ghostwalker18.scheduledesktop.converters.XMLStoLessonsConverter;
import com.ghostwalker18.scheduledesktop.database.AppDatabase;
import com.ghostwalker18.scheduledesktop.network.NetworkService;
import com.ghostwalker18.scheduledesktop.network.ScheduleNetworkAPI;
import com.ghostwalker18.scheduledesktop.system.XMLBundleControl;
import com.ghostwalker18.scheduledesktop.utils.Utils;
import com.github.pjfanning.xlsx.StreamingReader;
import com.github.pjfanning.xlsx.exceptions.OpenException;
import com.github.pjfanning.xlsx.exceptions.ParseException;
import com.github.pjfanning.xlsx.exceptions.ReadException;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import okhttp3.ResponseBody;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Workbook;
import org.javatuples.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.*;
import java.util.prefs.Preferences;

/**
 * Этот класс представляет собой репозиторий данных приложения.
 *
 * @author  Ипатов Никита
 */
public class ScheduleRepository {
    public static final String BASE_URI = "https://ptgh.onego.ru/9006/";
    private static final String MAIN_SELECTOR = "h2:contains(Расписание занятий и объявления:) + div > table > tbody";
    public static final String MONDAY_TIMES_URL =
            "https://r1.nubex.ru/s1748-17b/47698615b7_fit-in~1280x800~filters:no_upscale()__f44488_08.jpg";
    public static final String OTHER_TIMES_URL =
            "https://r1.nubex.ru/s1748-17b/320e9d2d69_fit-in~1280x800~filters:no_upscale()__f44489_bb.jpg";
    public static final String MONDAY_TIMES_PATH = "mondayTimes.jpg";
    public static final String OTHER_TIMES_PATH = "otherTimes.jpg";
    private final ResourceBundle strings = ResourceBundle.getBundle("strings",
            new XMLBundleControl());
    private final ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
            new XMLBundleControl());
    private final ScheduleNetworkAPI api;
    private final AppDatabase db;
    private final IConverter converter = new XMLStoLessonsConverter();
    private final Preferences preferences = ScheduleApp.getPreferences();
    private final List<Pair<String, File>> scheduleFiles = new LinkedList<>();
    private final BehaviorSubject<BufferedImage> mondayTimes = BehaviorSubject.create();
    private final BehaviorSubject<BufferedImage> otherTimes = BehaviorSubject.create();
    private final ReplaySubject<Status> status = ReplaySubject.create();
    private CompletableFuture<UpdateResult> updateResult;
    private final ExecutorService updateExecutorService = Executors.newFixedThreadPool(4);
    private final List<CompletableFuture<UpdateResult>> updateFutures = new ArrayList<>();
    private boolean allJobsDone = true;

    /**
     * Это перечисление показывает, чем завершилось обновление репозитория.
     */
    public enum UpdateResult {
        SUCCESS, FAIL;

        public static UpdateResult fromInt(int i){
            if (i == 0) {
                return SUCCESS;
            }
            return FAIL;
        }

        public static int toInt(UpdateResult result){
            if(result == SUCCESS)
                return 0;
            return 1;
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

    public ScheduleRepository(AppDatabase db, @NotNull NetworkService networkService){
        this.db = db;
        api = networkService.getScheduleAPI();
    }

    /**
     * Этот метод обновляет репозиторий приложения.
     * Метод использует многопоточность и может вызывать исключения в других потоках.
     * Требуется интернет соединение.
     */
    public void update(){
        String downloadFor = preferences.get("downloadFor", "all");
        if(allJobsDone) {
            allJobsDone = false;
            updateFutures.clear();
            if (downloadFor.equals("all") || downloadFor.equals("first"))
                updateFutures.add(
                        CompletableFuture.supplyAsync(this::updateFirstCorpus, updateExecutorService)
                );
            if (downloadFor.equals("all") || downloadFor.equals("second"))
                updateFutures.add(
                        CompletableFuture.supplyAsync(this::updateSecondCorpus, updateExecutorService)
                );
            updateFutures.add(
                    CompletableFuture.supplyAsync(this::updateTimes, updateExecutorService)
            );

            updateResult = CompletableFuture.allOf(updateFutures.toArray(new CompletableFuture[0]))
                    .thenApplyAsync(ignored -> {
                        allJobsDone = true;
                        for(CompletableFuture<UpdateResult> future : updateFutures){
                            if(future.getNow(UpdateResult.FAIL) == UpdateResult.FAIL){
                                preferences.put("previous_update_result",
                                        String.valueOf(UpdateResult.toInt(UpdateResult.FAIL)));
                                return UpdateResult.FAIL;
                            }
                        }
                        return UpdateResult.SUCCESS;
                    }, updateExecutorService);
        }
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
     * Этот метод используется для получения результата обновления репозитория.
     */
    public CompletableFuture<UpdateResult> onUpdateCompleted(){
        return updateResult;
    }

    /**
     * Этот метод используется для получения результата предыдущего обновления репозитория.
     */
    public UpdateResult getUpdateResult(){
        return UpdateResult.fromInt(preferences.getInt("previous_update_result", 0));
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
     * Этот метод возвращает всех учителей, упоминаемых в расписании.
     *
     * @return список учителей
     */
    public Observable<List<String>> getTeachers(){
        return db.lessonDao().getTeachers();
    }

    /**
     * Этот метод возвращает все группы, упоминаемые в расписании.
     *
     * @return список групп
     */
    public Observable<List<String>> getGroups(){
        return db.lessonDao().getGroups();
    }

    /**
     * Этот метод позволяет получить список всех предметов в расписании для заданной группы.
     * @param group группа
     * @return список предметов
     */
    public Observable<List<String>> getSubjects(String group) {
        return db.lessonDao().getSubjectsForGroup(group);
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
    @NotNull
    public Observable<List<Lesson>> getLessons(Calendar date, @Nullable String teacher, @Nullable String group){
        if (teacher != null && group != null)
            return db.lessonDao().getLessonsForGroupWithTeacher(date, group, teacher);
        else if (teacher != null)
            return db.lessonDao().getLessonsForTeacher(date, teacher);
        else if (group != null)
            return db.lessonDao().getLessonsForGroup(date, group);
        else return BehaviorSubject.createDefault(new LinkedList<>());
    }

    /**
     * Этот метод позволяет получить последнюю дату,
     * для которой для заданной группы указано расписание.
     * @param group группа
     * @return последняя дата, для которой существует расписание
     */
    public Calendar getLastKnownLessonDate(String group){
        return db.lessonDao().getLastKnownLessonDate(group);
    }

    /**
     * Этот метод получает ссылки с сайта ПАСТ,
     * по которым доступно расписание для корпуса на Первомайском проспекте.
     *
     * @return список ссылок
     */
    @NotNull
    public  List<String> getLinksForSecondCorpusSchedule(){
        List<String> links = new ArrayList<>();
        try{
            Document doc = api.getMainPage().execute().body();
            Elements linkElements = doc.select(MAIN_SELECTOR).get(0)
                    .select("tr").get(1)
                    .select("td").get(1)
                    .select("a");
            for(Element linkElement : linkElements){
                if(linkElement.attr("href").endsWith(".xlsx"))
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
     * по которым доступно расписание для корпуса на Мурманской улице.
     *
     * @return список ссылок
     */
    public List<String> getLinksForFirstCorpusSchedule(){
        List<String> links = new ArrayList<>();
        try{
            Document doc = api.getMainPage().execute().body();
            Elements linkElements = doc.select(MAIN_SELECTOR).get(0)
                    .select("tr").get(1)
                    .select("td").get(0)
                    .select("a");
            for(Element linkElement : linkElements){
                if(linkElement.attr("href").endsWith(".xlsx"))
                    links.add(linkElement.attr("href"));
            }
            return links;
        }
        catch(IOException e){
            return links;
        }
    }

    /**
     * Этот метод возвращает пары название файла / содержимое файла для всех скачанных файлов расписания.
     * @return название файла / содержимое файла
     */
    public List<Pair<String, File>> getScheduleFiles(){
        return scheduleFiles;
    }

    /**
     * Этот метод предназначен для сохранения последней выбранной группы перед закрытием приложения.
     * @param group группа для сохранения
     */
    public void saveGroup(String group){
        preferences.put("savedGroup", group);
    }

    /**
     * Этот метод возвращает сохраненную группу.
     * @return группа
     */
    public String getSavedGroup(){
        return preferences.get("savedGroup", platformStrings.getString("combox_placeholder"));
    }

    private UpdateResult updateTimes() {
        File mondayTimesFile = new File(MONDAY_TIMES_PATH);
        File otherTimesFile = new File(OTHER_TIMES_PATH);
        if (!preferences.getBoolean("doNotUpdateTimes", true)
                || !mondayTimesFile.exists() || !otherTimesFile.exists()) {
            api.getMondayTimes().enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try (ResponseBody body = response.body()) {
                        BufferedImage image = ImageIO.read(body.byteStream());
                        mondayTimes.onNext(image);
                        ImageIO.write(image, "jpg", mondayTimesFile);
                    } catch (Exception ignored) {/*Not required*/}
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {/*Not required*/}
            });
            api.getOtherTimes().enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try (ResponseBody body = response.body()) {
                        BufferedImage image = ImageIO.read(body.byteStream());
                        otherTimes.onNext(image);
                        ImageIO.write(image, "jpg", otherTimesFile);
                    } catch (Exception ignored) {/*Not required*/}
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {/*Not required*/}
            });
        } else {
            try {
                BufferedImage bitmap1 = ImageIO.read(mondayTimesFile);
                mondayTimes.onNext(bitmap1);
                BufferedImage bitmap2 = ImageIO.read(otherTimesFile);
                otherTimes.onNext(bitmap2);
            } catch (Exception ignored) {/*Not required*/}
        }
        return UpdateResult.SUCCESS;
    }

    /**
     * Этот метод используется для обновления БД приложения занятиями для первого корпуса
     */
    private UpdateResult updateFirstCorpus(){
        return updateSchedule(this::getLinksForFirstCorpusSchedule, converter::convertFirstCorpus);
    }

    /**
     * Этот метод используется для обновления БД приложения занятиями для второго корпуса
     */
    private UpdateResult updateSecondCorpus(){
        return updateSchedule(this::getLinksForSecondCorpusSchedule, converter::convertSecondCorpus);
    }

    /**
     * Этот метод используется для обновления БД приложения занятиями
     * @param linksGetter метод для получения ссылок на файлы расписания
     * @param parser парсер файлов расписания
     */
    private UpdateResult updateSchedule(Callable<List<String>> linksGetter, IConverter.IConversion parser){
        List<String> scheduleLinks;
        try {
            scheduleLinks = linksGetter.call();
        } catch (Exception e){
            return UpdateResult.FAIL;
        }
        if(scheduleLinks.isEmpty()){
            status.onNext(new Status(strings.getString("schedule_download_error"), 0));
            return UpdateResult.FAIL;
        }
        final List<UpdateResult> successCounter = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(scheduleLinks.size());
        for(String link : scheduleLinks){
            status.onNext(new Status(strings.getString("schedule_download_status"), 10));
            api.getScheduleFile(link).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    ZipSecureFile.setMinInflateRatio(0.005);
                    synchronized (status){
                        status.onNext(new Status(strings.getString("schedule_opening_status"), 33));
                    }
                    try(ResponseBody body = response.body();
                        InputStream stream = body.byteStream();
                        Workbook excelFile = StreamingReader.builder()
                                .rowCacheSize(10)
                                .bufferSize(10485670)
                                .open(stream)
                    ){
                        synchronized (status){
                            status.onNext(new Status(strings.getString("schedule_parsing_status"), 50));
                        }
                        File scheduleFile = Files.createTempFile(null, ".tmp").toFile();
                        scheduleFile.deleteOnExit();
                        Files.copy(stream, scheduleFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        scheduleFiles.add(new Pair<>(Utils.getNameFromLink(link), scheduleFile));
                        List<Lesson> lessons = parser.convert(excelFile);
                        db.lessonDao().insertMany(lessons);
                        synchronized (status){
                            status.onNext(new Status(strings.getString("processing_completed_status"), 100));
                        }
                        successCounter.add(UpdateResult.SUCCESS);
                    }
                    catch(OpenException | ReadException | ParseException e){
                        synchronized (status){
                            status.onNext(new Status(strings.getString("schedule_opening_error"), 0));
                        }
                    }
                    catch (Exception e){
                        synchronized (status){
                            status.onNext(new Status(strings.getString("schedule_parsing_error"), 0));
                        }
                    }
                    finally {
                        latch.countDown();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    latch.countDown();
                    status.onNext(new Status(strings.getString("schedule_download_error"), 0));
                }
            });
        }
        try{
            latch.await();
            if(successCounter.size() == scheduleLinks.size())
                return UpdateResult.SUCCESS;
            else
                return UpdateResult.FAIL;
        }
        catch (InterruptedException | ThreadDeath e){
            Thread.currentThread().interrupt();
            return UpdateResult.FAIL;
        }
        catch (Exception e) {
            return UpdateResult.FAIL;
        }
    }
}