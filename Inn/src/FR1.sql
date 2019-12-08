select r.*, p.popularity as "Room Popularity", 
c.nextCheckin as "Next Available Check-in Date", 
l.stay as "Most Recent Stay",
l.checkout as "Most Recent Checkout" from lab7_rooms r
join 
    (select room, round(sum(
        if(datediff(curdate(), checkin) <= 180, 
            datediff(checkout, checkin),
            datediff(checkout, 
                     date_sub(curdate(), INTERVAL 180 DAY)))) / 180, 2) as popularity
        from lab7_reservations
            where datediff(curdate(), checkout) <= 180 
            group by room) p
join 
    (select g1.room, min(g1.checkout) as nextCheckin from         
        (select * from lab7_reservations 
            where checkin > curdate()) as g1
    left outer join
        (select * from lab7_reservations 
            where checkin > curdate()) as g2
    on g1.room = g2.room and
        g1.code <> g2.code and
        g1.checkout = g2.checkin
    where g2.room is null
    group by g1.room) as c
join 
    (select g1.room, datediff(g1.checkout, g1.checkin) as stay, g1.checkout from 
    (select * from lab7_reservations
        where checkout < curdate()) as g1
left outer join
    (select * from lab7_reservations
        where checkout < curdate()) as g2
on g1.room = g2.room and
    g1.checkout < g2.checkout
where g2.room is null) as l
on r.roomcode = p.room and
    r.roomcode = c.room and
    r.roomcode = l.room
order by p.popularity desc
