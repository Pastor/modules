#!/bin/sh

export SECONDS=3600
echo this script captures for $SECONDS seconds

echo remove the vmstat.rrd database in this directory
rm vmstat.rrd

echo  create vmstat.rrd for 10000 seconds = over 27 hours max at 1 second captures
rrdtool create vmstat.rrd --step 1  \
DS:r:GAUGE:5:U:U \
DS:b:GAUGE:5:U:U \
DS:avm:GAUGE:5:U:U \
DS:fre:GAUGE:5:U:U \
DS:re:GAUGE:5:U:U \
DS:pi:GAUGE:5:U:U \
DS:po:GAUGE:5:U:U \
DS:fr:GAUGE:5:U:U \
DS:sr:GAUGE:5:U:U \
DS:cy:GAUGE:5:U:U \
DS:in:GAUGE:5:U:U \
DS:st:GAUGE:5:U:U \
DS:cs:GAUGE:5:U:U \
DS:us:GAUGE:5:U:U \
DS:sy:GAUGE:5:U:U \
DS:id:GAUGE:5:U:U \
DS:wa:GAUGE:5:U:U \
DS:pc:GAUGE:5:U:U \
DS:ec:GAUGE:5:U:U \
RRA:AVERAGE:0.5:1:100000

echo Note the vmstat sy faults coloumn is renames st so sy is system time

TIME=`date +%s`
echo startseconds $TIME

echo Capturing for $SECONDS seconds
vmstat 1 $SECONDS >vmstat.txt &
vmstat 1 $SECONDS | awk -v time=$TIME '/^.[0-9]/{ n++; print "rrdtool update vmstat.rrd "time+n":" $1 ":" $2 ":" $3 ":" $4 ":" $5 ":" $6 ":" $7 ":" $8 ":" $9 ":" $10 ":" $11 ":" $12 ":" $13 ":" $14 ":" $15 ":" $16 ":" $17 ":" $18 ":" $19 }' >vmstat.output

ENDTIME=`date +%s`
echo endseconds $ENDTIME

echo load the vmstat data into the vmstat.rrd database
echo the file has `wc -l vmstat.output` lines
ksh <./vmstat.output

echo graph the data
rrdtool graph cpu_utilisation.gif \
--rigid --lower-limit 0 --upper-limit 100 \
--title "CPU Utilisation" \
--vertical-label "Percent Stacked" \
--start $TIME \
--end $ENDTIME \
--height 300 \
DEF:us=vmstat.rrd:us:AVERAGE AREA:us#00FF00:"User" \
DEF:sy=vmstat.rrd:sy:AVERAGE STACK:sy#0000FF:"System" \
DEF:wa=vmstat.rrd:wa:AVERAGE STACK:wa#FF0000:"Wait" \
DEF:id=vmstat.rrd:id:AVERAGE STACK:id#FFFFFF:"Idle" 

rrdtool graph process_queue.gif \
--title "Process Run Queue" \
--vertical-label "Processes" \
--height 300 \
--start $TIME \
--end $ENDTIME \
DEF:r=vmstat.rrd:r:AVERAGE LINE2:r#00FF00:"Process Queue"

rrdtool graph process_io.gif \
--title "Process wait IO" \
--vertical-label "Processes" \
--height 300 \
--start $TIME \
--end $ENDTIME \
DEF:r=vmstat.rrd:b:AVERAGE LINE2:r#00FF00:"Process Queue"

rrdtool graph process_io.gif \
--title "Free memory" \
--vertical-label "Memory" \
--height 300 \
--start $TIME \
--end $ENDTIME \
DEF:r=vmstat.rrd:fre:AVERAGE LINE2:r#00FF00:"Free memory"


rrdtool graph physical_consumed.gif \
--title "Physical CPU Consumed" \
--vertical-label "CPUs" \
--height 300 \
--start $TIME \
--end $ENDTIME \
DEF:pc=vmstat.rrd:pc:AVERAGE LINE2:pc#00FF00:"Physical Consumed"

rrdtool graph entitlement_consumed.gif \
--title "Entitlement CPU Consumed" \
--vertical-label "CPUs" \
--height 300 \
--start $TIME \
--end $ENDTIME \
DEF:ec=vmstat.rrd:ec:AVERAGE LINE2:ec#00FF00:"Entitlement Consumed"

echo images available
ls -l *.gif

