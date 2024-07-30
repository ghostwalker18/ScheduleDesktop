package com.ghostwalker18.scheduledesktop;

import io.reactivex.rxjava3.core.Observable;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

public class AppDatabase {

    private static AppDatabase instance = null;
    private SessionFactory sessionFactory;
    public static AppDatabase getInstance(){
        if(instance == null)
            instance = new AppDatabase();
        return instance;
    }

    private AppDatabase(){
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("com/ghostwalker18/scheduledesktop/hibernate.cfg.xml")
                .build();

        MetadataSources sources = new MetadataSources(registry);
        sources.addAnnotatedClass(Lesson.class)
                .addPackage(Lesson.class.getPackage());

        MetadataBuilder metadataBuilder = sources.getMetadataBuilder();
        metadataBuilder.applyAttributeConverter(DateConverters.class);

        Metadata metadata = metadataBuilder.build();

        sessionFactory = metadata.getSessionFactoryBuilder()
                .build();
    }

    public void insertMany(List<Lesson> lessons){

    }

    public void update(Lesson lesson){

    }

    public Observable<String[]> getTeachers(){
        return null;
    }

    public Observable<String[]> getGroups(){
        return null;
    }

    public Observable<Lesson[]> getLessonsForGroupWithTeacher(String group, String teacher){
        return null;
    }

    public Observable<Lesson[]> getLessonsForGroup(String group){
        return null;
    }

    public Observable<Lesson[]> getLessonsForTeacher(String teacher){
        return null;
    }
}