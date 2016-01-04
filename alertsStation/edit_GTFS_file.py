import codecs
import re


#-------delete first line-------
'''
print "delete first line"

#delete first line and create new file such as new______.
with codecs.open('./resources/agency.txt', "r", "utf-8-sig") as fin:
    data = fin.read().splitlines(True)
with codecs.open('./resources/agency.txt', "w", "utf-8-sig") as fout:
    fout.writelines(data[1:])

with codecs.open('./resources/routes.txt', "r", "utf-8-sig") as fin:
    data = fin.read().splitlines(True)
with codecs.open('./resources/routes.txt', "w", "utf-8-sig") as fout:
    fout.writelines(data[1:])

with codecs.open('./resources/stop_times.txt', "r", "utf-8-sig") as fin:
    data = fin.read().splitlines(True)
with codecs.open('./resources/stop_times.txt', "w", "utf-8-sig") as fout:
    fout.writelines(data[1:])

with codecs.open('./resources/stops.txt', "r", "utf-8-sig") as fin:
    data = fin.read().splitlines(True)
with codecs.open('./resources/stops.txt', "w", "utf-8-sig") as fout:
    fout.writelines(data[1:])

with codecs.open('./resources/trips.txt', "r", "utf-8-sig") as fin:
    data = fin.read().splitlines(True)
with codecs.open('./resources/trips.txt', "w", "utf-8-sig") as fout:
    fout.writelines(data[1:])

print "end delete first line"
'''
#-------end delete first line-------


#-------agency-------
'''
print "agency start"

# agency.txt
# 0 - agency_id *
# 1 - agency_name *
# 2 - agency_url
# 3 - agency_timezone
# 4 - agency_lang
# 5 - agency_phone
# 6 - agency_fare_url

# new_agency.txt
# 0 - agency_id
# 1 - agency_name

# 3: egged, 5: dan, 15: metropoline, 16: superbus
agency = ['3','5','15','16']

inputFile = codecs.open('./resources/agency.txt', "r", "utf-8-sig")
outputFile = codecs.open('./resources/new_agency.txt', "wb", "utf-8-sig")

for line in inputFile:
    words = line.split(",")
    if words[0] in agency:
        print words[0]+','+words[1]
        outputFile.write(words[0]+','+words[1]+u'\r\n')

outputFile.close()
inputFile.close()

print "end agency"
'''
#-------end agency-------


#-------routes-------
'''
print "routes"

# routes.txt
# 0 - route_id *
# 1 - agency_id *
# 2 - route_short_name *
# 3 - route_long_name *
# 4 - route_desc *
# 5 - route_type *
# 6 - route_color

# new_routes.txt
# 0 - route_id
# 1 - agency_id
# 2 - route_short_name
# 3 - route_long_name
# 4 - route_desc
# 5 - route_type
# 6 - origin_city_name  (new column)

agencyFile = codecs.open('./resources/new_agency.txt', "r", "utf-8-sig")
inputFile = codecs.open('./resources/routes.txt', "r", "utf-8-sig")
outputFile = codecs.open('./resources/new_routes.txt', "wb", "utf-8-sig")

agency = []
for line in agencyFile:
    words = line.split(",")
    agency.append(words[0])

for line in inputFile:
    words = line.split(",")
    route_long_name = re.split('<->',words[3])
    origin_city = re.split('-',route_long_name[0])
    if words[1] in agency:
        print words[0]+','+words[1]+','+words[2]+','+words[3]+','+words[4]+','+words[5]+','+origin_city[-1]
        outputFile.write(words[0]+','+words[1]+','+words[2]+','+words[3]+','+words[4]+','+words[5]+','+origin_city[-1]+u'\r\n')

agencyFile.close()
outputFile.close()
inputFile.close()

print "end routes"
'''
#-------end routes-------


#-------trips-------
'''
print "trips"

# trips.txt
# 0 - route_id *
# 1 - service_id
# 2 - trip_id *
# 3 - direction_id *
# 4 - shape_id

# new_trips.txt
# 0 - route_id
# 1 - trip_id
# 2 - direction_id

routesFile = codecs.open('./resources/new_routes.txt', "r", "utf-8-sig")
inputFile = codecs.open('./resources/trips.txt', "r", "utf-8-sig")
outputFile = codecs.open('./resources/new_trips.txt', "wb", "utf-8-sig")

routes = []

for line in routesFile:
    words = line.split(",")
    routes.append(words[0])

for line in inputFile:
    words = line.split(",")
    if words[0] in routes:
        print words[0]+','+words[2]+','+words[3]
        outputFile.write(words[0]+','+words[2]+','+words[3]+u'\r\n')


routesFile.close()
outputFile.close()
inputFile.close()
print "end trips"
'''
#-------end trips-------


#-------stop_times-------
'''
print "stop_times"

# stop_times.txt
# 0 - trip_id *
# 1 - arrival_time
# 2 - departure_time
# 3 - stop_id *
# 4 - stop_sequence *
# 5 - pickup_type
# 6 - drop_off_type

# new_trips.txt
# 0 - trip_id
# 1 - stop_id
# 2 - stop_sequence

tripsFile = codecs.open('./resources/new_trips.txt', "r", "utf-8-sig")
inputFile = codecs.open('./resources/stop_times.txt', "r", "utf-8-sig")
outputFile = codecs.open('./resources/new_stop_times.txt', "wb", "utf-8-sig")

trips = []


for line in tripsFile:
    words = line.split(",")
    trips.append(words[1])

for line in inputFile:
    words = line.split(",")
    if words[0] in trips:
        print words[0]+','+words[3]+','+words[4]
        outputFile.write(words[0]+','+words[3]+','+words[4]+u'\r\n')


tripsFile.close()
outputFile.close()
inputFile.close()
print "end stop_times"
'''
#-------end stop_times-------


#-------stops-------
'''
print "stops"
# stop_times.txt
# 0 - stop_id *
# 1 - stop_code
# 2 - stop_name *
# 3 - stop_desc
# 4 - stop_lat *
# 5 - stop_lon *
# 6 - location_type
# 7 - parent_station

# new_trips.txt
# 0 - stop_id
# 1 - stop_name
# 2 - stop_lat
# 2 - stop_lon

stop_timesFile = codecs.open('./resources/stop_times.txt', "r", "utf-8-sig")
inputFile = codecs.open('./resources/stops.txt', "r", "utf-8-sig")
outputFile = codecs.open('./resources/new_stops.txt', "wb", "utf-8-sig")

stop_times = []


for line in stop_timesFile:
    words = line.split(",")
    if words[1] not in words:
        stop_times.append(words[1])

for line in inputFile:
    words = line.split(",")
    if words[0] in stop_times:
        print words[0]+','+words[2]+','+words[4]+','+words[5]
        outputFile.write(words[0]+','+words[2]+','+words[4]+','+words[5]+u'\r\n')


stop_timesFile.close()
outputFile.close()
inputFile.close()
print "end stops"
'''
#-------end stops-------