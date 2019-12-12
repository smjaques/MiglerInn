select room, month, sum(rate * nights) as rev from 
    (select * from 
    (select room, rate, monthname(checkin) as month,
    datediff(least(checkout,
            date_add(last_day(checkin), interval 1 day)),
            checkin) as nights
            from lab7_reservations) as d
    union
    (select room, rate, monthname(checkout) as month,
    datediff(checkout, greatest(checkin, 
                date_add(last_day(checkin), 
                interval 1 day))) as nights
            from lab7_reservations)) g
    where nights > 0
    group by room, month
    order by room
