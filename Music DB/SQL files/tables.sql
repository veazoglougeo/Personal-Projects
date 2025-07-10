create table "customer"(
	id int primary key,
	first_name varchar(100),
	last_name varchar(100),
	company varchar(100),
	address varchar(100),
	city varchar(100),
	area varchar(100),
	country varchar(100),
	postal_code integer,
	tel_no varchar(100),
	fax varchar(100),
	email varchar(100),
	extra integer 
)

create table "invoice"(
	id integer primary key,
	customer_id integer references customer(id),
	date DATE,
	bill_address varchar(100),
	bill_city varchar(100),
	bill_area varchar(100),
	bill_country varchar(100),
	bill_postal_code varchar(100),
	amount decimal	
)

create table "artist"(
	id integer primary key,
	name varchar(100)
)

create table "album"(
	id integer primary key,
	title varchar(100),
	artist_id integer references artist(id)
)

create table "track"(
	id integer primary key,
	name varchar(500),
	album_id integer references album(id),
	media_type varchar(100),
	genre varchar(100),
	creator varchar(500),
	duration integer,
	byte_size integer,
	price decimal
)

create table "invoiceline"(
	id integer primary key,
	invoice_id integer references invoice(id),
	track_id integer references track(id),
	price decimal,
	quantity integer
)
