create table if not exists post(
   id serial primary key,
   name varchar(2000),
   text text,
   link varchar(300),
   created timestamp
);