a. 
select distinct c.id, c.first_name, c.last_name
from customer c
join invoice i on c.id = i.customer_id
where i.date between '2012-09-01' and '2012-10-10'

b.
select t.id, t.name
from track t
left outer join invoiceline i on t.id = i.track_id
where i.track_id is null

c.
select c.id, c.first_name, c.last_name, count(i.id) as purchases, sum(i.amount)
from customer c
join invoice i on c.id = i.customer_id
group by c.id
order by sum(i.amount) desc
fetch first 1 row only

d.
select a.id, a.name, count(i.id) as sales, sum(i.price * i.quantity) as amount
from artist a
join album al on a.id = al.artist_id
join track on al.id = track.album_id
join invoiceline i on track.id = i.track_id
group by a.id
order by amount desc

e.
select distinct t.id, t.name, t.creator
from track t
join invoiceline il on t.id = il.track_id
join invoice i on il.invoice_id = i.id
where i.customer_id = 1

f.
select i.id, i.date , i.bill_postal_code, i.amount
from invoice i
join invoiceline il on i.id = il.invoice_id
join track t on il.track_id = t.id
group by i.id
having count(il.track_id) > 2
