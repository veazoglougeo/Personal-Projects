copy artist(id, name) 
from 'C:\Users\veazo\Desktop\artist.csv'
delimiter ','

copy album(id, title, artist_id) 
from 'C:\Users\veazo\Desktop\album.csv' 
delimiter ','

copy customer(id, first_name, last_name, company, address, city, area, country, postal_code, tel_no, fax, email, extra) 
from 'C:\Users\veazo\Desktop\customer.csv' 
delimiter ','

alter table customer
drop column extra;

copy invoice(id, customer_id, date, bill_address, bill_city, bill_area, bill_country, bill_postal_code, amount) 
from 'C:\Users\veazo\Desktop\invoice.csv' 
delimiter ','

copy invoiceline(id, invoice_id, track_id, price, quantity) 
from 'C:\Users\veazo\Desktop\invoiceline.csv' 
delimiter ','

copy track(id, name, album_id, media_type, genre, creator, duration, byte_size, price) 
from 'C:\Users\veazo\Desktop\trakcs.csv' 
delimiter ','