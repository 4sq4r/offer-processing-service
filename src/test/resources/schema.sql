drop table if exists warehouses cascade;
create table warehouses
(
    id         varchar(36) not null primary key,
    name       varchar(255) not null,
    merchant_id varchar(36) not null references merchants (id),
    city_id varchar(36) not null references cities (id),
    created_at timestamp,
    updated_at timestamp
);

drop table if exists cities cascade;
create table cities
(
    id         varchar(36) not null primary key,
    name       varchar(255) not null,
    created_at timestamp,
    updated_at timestamp
);

drop table if exists merchants cascade;
create table merchants
(
    id         varchar(36) not null primary key,
    name       varchar(255) not null,
    created_at timestamp,
    updated_at timestamp
);
