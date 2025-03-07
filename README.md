[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=ghostwalker18_ScheduleDesktop&metric=alert_status)](https://sonarcloud.io/dashboard?id=ghostwalker18_ScheduleDesktop)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=ghostwalker18_ScheduleDesktop&metric=bugs)](https://sonarcloud.io/summary/new_code?id=ghostwalker18_ScheduleDesktop)
[![Code smells](https://sonarcloud.io/api/project_badges/measure?project=ghostwalker18_ScheduleDesktop&metric=code_smells)](https://sonarcloud.io/dashboard?id=ghostwalker18_ScheduleDesktop)
[![Github All Releases](https://img.shields.io/github/downloads/ghostwalker18/ScheduleDesktop/total.svg)]()
# Расписание Петрозаводского архитектурно-строительного техникума.
![Расписание занятий](https://github.com/ghostwalker18/ScheduleDesktop/blob/master/promo_images/schedule1.png?raw=true)

Неофициальное десктопное приложение расписания ПАСТ. Приложение представляет собой клиент, который скачивает файлы расписания с официального сайта  Петрозаводского архитектурно-строительного техникума (бывшего ПТГХ) и представляет их содержимое в удобной для пользователя форме.
<br>
Для пользователя доступны:
<ul>
  <li>Поиск расписания для отдельной группы и/или преподавателя</li>
  <li>Просмотр расписания звонков</li>
  <li>Возможность поделиться как расписанием занятий, так и расписанием звонков</li>
  <li>Возможность скачать исходные Excel-файлы расписания</li>
</ul>

## Установка
Для установки скачать нужный [релиз](https://github.com/ghostwalker18/ScheduleDesktop/releases/latest).
### Релизы
<ul>
  <li>Java версия - ScheduleDesktop-Java.rar</li>
  <li>Windows версия - SchedulePCME-2.3.msi</li>
</ul>

### Java
После скачивания распаковать rar архив в удобном месте. Для запуска используется двойной щелчок ЛКМ по файлу с расширением .jar или же по ярлыку, который вы можете создать сами. Все файлы после распаковки должны находится в одном месте, в той же папке.

### Windows
После скачивания установщика (.msi) двойным щелчком ЛКМ запустить его и установить программу в предложенном месте. После установки программы установщик может быть удален. После установки на рабочем столе появится ярлык, используемый для запуска программы.

## Использование
При запуске приложения для пользователя открывается вкладка **Расписание занятий** с расписанием на текущую неделю. Неделю можно менять кнопками **Назад** для выбора предыдущей недели и **Вперед** для выбора следующей недели.
<br>
С помощью полей, подписанных слева как **Выберете группу** и **Выберете преподавателя**, пользователь может выбрать групппу и/или преподавателя, после чего при нажатии на кнопку с датой откроется таблица с расписанием на этот день для заданной комбинации. Таблица с расписанием на сегодняшний день открыта по умолчанию.
<br>
Внизу вкладки расположена полоса загрузки, показывающая на прогресс обновления расписания, а также над ней расположен тест, описывающий статус обновления расписания.
<br>
На вкладке **Расписание звонков** пользователю доступны фотографии с расписанием звонков на понедельник и другие дни.
<br><br>
Прочие функции приложения:
<ul>
  <li> <b>Настройки приложения</b> - при нажатии на кнопку Настройки открывается окно, где пользователь может настроить тему приложения(светлую или темную), язык приложения(русский, белоруский, украинский, казахский, английский) и скачивать ли каждый раз при обновлении расписания новое расписание звонков. Настрйоки вступают в в силу после перезапуска приложения, если была нажата кнопка сохранить</li>
  <li> <b>Поделиться расписанием</b> - при нажатии на кнопку Поделиться на вкладке Расписание в буфер обмена копируется расписание для открытых дней в виде обычного текста, при нажатии на вкладке Расписание звонков в буфер обмена копируется 2 файла с фотографиями расписания звонков. Содержимое буфера обмена можно обычным путем (например Ctrl-V) вставить, например, в текст сообщения в мессенджере или эл.почте </li>
  <li> <b>Скачать файлы расписание</b> - при нажатии на кнопку Скачать файл расписания открывается окно выбора директории в которой пользователь может сохранить скачанные оригинальные Excel-файлы расписания.</li>
</ul>

## Требования
Для запуска Java версии требуется установленная на компьютере Java, с версией не ниже 8.
Вы можете скачать и установить её с сайта https://learn.microsoft.com/ru-ru/java/openjdk/download
