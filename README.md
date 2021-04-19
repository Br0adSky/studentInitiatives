# StudentInitiatives
Проект студента группы НМТ-473511 Зозули Артема.
Данное приложение позволяет студентам УрФУ предложить идею нового проекта или инициативу по решению проблемы в университете и получить помощь в её реализации. 
Приложение добавлено на сервер и доступно по следующей [ссылке](http://46.173.218.68/). (ссылка на старую версию не работает, тк убита, чтобы оперативная память не кончилась).
Логин для администратора - Admin@admin, пароль admin.
## Функции приложения
- Каждый пользователь может добавлять свои и редактировать свои заявки (если они не в статусе голосования);
- Каждый  пользователь может нажать на заявку в списке всех заявок и открыть список комментариев к ней;
- Каждый пользователь может найти необходимую ему заявку с помощью соотвествующих фильтров;
- Каждый пользователь может голосовать за или против выбранной им заявки;
- В приложении присутствует система разделения ролей, согласно заданию.
### Схемы
На рисунке 1 изображена архитектура приложения. Все схемы также доступны в репозитории. На рисунке 2 изображены связи между таблицами базы данных.
![](https://raw.githubusercontent.com/Br0adSky/studentInitiatives/main/src/%D0%92%D0%B7%D0%B0%D0%B8%D0%BC%D0%BE%D0%B4%D0%B5%D0%B9%D1%81%D1%82%D0%B2%D0%B8%D0%B5%20%D0%BA%D0%BB%D0%B0%D1%81%D1%81%D0%BE%D0%B2.png)
Рисунок 1 - Архитектура приложения
![](https://raw.githubusercontent.com/Br0adSky/studentInitiatives/main/entitys.png)

Рисунок 2 - Связи между таблицами БД
### Инструменты
Работа была выполнена с помощью следующих инструментов: Spring Boot, Spring Security, Spring Validation, Thymeleaf, Hibernate и PostgreSQL.
### Запуск на сервере
Если возникнут какие-то ошибки при запуске на новом сервере, связанные с созданием таблиц баз данных, то необходимо выполнить файл init.sql.
