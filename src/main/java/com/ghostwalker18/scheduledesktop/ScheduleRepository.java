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

import io.reactivex.rxjava3.core.Observable;
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
import java.util.List;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

public class ScheduleRepository {

    private static ScheduleRepository repository = null;
    private  final Preferences preferences = Preferences.userNodeForPackage(ScheduleRepository.class);
    private final ScheduleNetworkAPI api;
    private final String baseUri = "https://ptgh.onego.ru/9006/";
    private final String mainSelector = "h2:contains(Расписание занятий и объявления:) + div > table > tbody";
    private final String mondayTimesPath = "mondayTimes.jpg";
    private final String otherTimesPath = "otherTimes.jpg";

    private final PublishSubject<BufferedImage> mondayTimes = PublishSubject.create();
    private final PublishSubject<BufferedImage> otherTimes = PublishSubject.create();
    private final PublishSubject<Status> status = PublishSubject.create();

    public static ScheduleRepository getRepository() throws Exception {
        if(repository == null)
                repository = new ScheduleRepository();
        return repository;
    }

    private ScheduleRepository() throws Exception {
        api = new Retrofit.Builder()
                .baseUrl(baseUri)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .build()
                .create(ScheduleNetworkAPI.class);
    }

    public Preferences getPreferences(){
        return preferences;
    }

    public Observable<Status> getStatus(){
        return status;
    }

    public Observable<BufferedImage> getMondayTimes(){
        return mondayTimes;
    }

    public Observable<BufferedImage> getOtherTimes(){
        return otherTimes;
    }

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
                        };
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

    public String getLinkForScheduleSecondCorpusMain(){
        try{
            Document doc = Jsoup.connect(baseUri).get();
            Element linkElement = doc.select(mainSelector).get(0)
                    .select("tr").get(1)
                    .select("td").get(0)
                    .select("p > a").get(0);
            String link = linkElement.attr("href");
            return link;
        }
        catch (IOException e){
            return null;
        }
    }

    public static class Status{
        public String text;
        public int progress;

        public Status(String text, int progress){
            this.text = text;
            this.progress = progress;
        }
    }
}