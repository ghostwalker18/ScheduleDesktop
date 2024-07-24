package com.ghostwalker18.scheduledesktop;

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ScheduleRepository {

    private static ScheduleRepository repository = null;
    private final ScheduleNetworkAPI api;
    private final String baseUri = "https://ptgh.onego.ru/9006/";
    private final String mainSelector = "h2:contains(Расписание занятий и объявления:) + div > table > tbody";

    public ScheduleRepository getRepository(){
        if(repository == null)
                repository = new ScheduleRepository();
        return repository;
    }

    private ScheduleRepository(){
        api = new Retrofit.Builder()
                .baseUrl(baseUri)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .build()
                .create(ScheduleNetworkAPI.class);
    }

    public void update(){
        new Thread(() -> {
            List<String> scheduleLinks = getLinksForScheduleFirstCorpus();
            if(scheduleLinks.size() == 0)
            for(String link : scheduleLinks){
                api.getScheduleFile(link).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.body() != null){
                            try(XSSFWorkbook excelFile = new XSSFWorkbook(response.body().byteStream())){
                                List<Lesson> lessons = XMLStoLessonsConverter.convertFirstCorpus(excelFile);
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

    public static class Status{
        public String text;
        public int progress;

        public Status(String text, int progress){
            this.text = text;
            this.progress = progress;
        }
    }
}