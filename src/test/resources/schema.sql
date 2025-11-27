drop table if exists cities cascade;
create table cities (
    id varchar(255) primary key,
    name varchar(255) not null unique,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);