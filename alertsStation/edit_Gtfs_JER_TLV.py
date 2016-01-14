__author__ = 'Yaacov'
import codecs
import re
import itertools





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

# 3: egged, 5: dan
agency = ['3','5']

inputFile = codecs.open('./resources/agency.txt', "r", "utf-8-sig")
outputFile = codecs.open('./resources/JER_TLV/agency_JER_TLV.txt', "wb", "utf-8-sig")

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


agencyFile = codecs.open('./resources/JER_TLV/agency_JER_TLV.txt', "r", "utf-8-sig")
inputFile = codecs.open('./resources/new_routes.txt', "r", "utf-8-sig")
outputFile = codecs.open('./resources/JER_TLV/routes_JER_TLV.txt', "wb", "utf-8-sig")
citiesFile = codecs.open('./resources/JER_TLV/cities_JER_TLV.txt', "r", "utf-8-sig")


agency = []
for line in agencyFile:
    words = line.split(",")
    agency.append(words[0])
    print words[0]

cities = []
for line in citiesFile:
    words = line.split(",")
    cities.append(words[0])


for line in inputFile:
    words = line.split(",")
    city = re.split('-',words[3])
    city[1] = city[1][:-1]
    #print city[1] origin_city
    #print city[3] destination_city

    if words[1] in agency:
        if city[1] == city[3]:
            if city[1] in cities:
                print words[0]+','+words[1]+','+words[2]+','+words[3]+','+words[4]+','+words[5]+','+city[1]
                outputFile.write(words[0]+','+words[1]+','+words[2]+','+words[3]+','+words[4]+','+words[5]+','+city[1]+u'\r\n')

agencyFile.close()
outputFile.close()
inputFile.close()
citiesFile.close()

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

routesFile = codecs.open('./resources/JER_TLV/routes_JER_TLV.txt', "r", "utf-8-sig")
inputFile = codecs.open('./resources/new_trips.txt', "r", "utf-8-sig")
outputFile = codecs.open('./resources/JER_TLV/trips_JER_TLV.txt', "wb", "utf-8-sig")

routes = []

for line in routesFile:
    words = line.split(",")
    routes.append(words[0])

for line in inputFile:
    words = line.split(",")
    if words[0] in routes:
        print words[0]+','+words[1]+','+words[2]
        outputFile.write(words[0]+','+words[1]+','+words[2])


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

tripsFile = codecs.open('./resources/JER_TLV/trips_JER_TLV.txt', "r", "utf-8-sig")
inputFile = codecs.open('./resources/new_stop_times.txt', "r", "utf-8-sig")
outputFile = codecs.open('./resources/JER_TLV/stop_times_JER_TLV.txt', "wb", "utf-8-sig")

trips = []


for line in tripsFile:
    words = line.split(",")
    trips.append(words[1])

for line in inputFile:
        words = line.split(",")
        if words[0] in trips:
            print words[0]+','+words[1]+','+words[2]
            outputFile.write(words[0]+','+words[1]+','+words[2])


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
# 3 - stop_lon

stop_timesFile = codecs.open('./resources/JER_TLV/stop_times_JER_TLV.txt', "r", "utf-8-sig")
inputFile = codecs.open('./resources/new_stops.txt', "r", "utf-8-sig")
outputFile = codecs.open('./resources/JER_TLV/stop_JER_TLV.txt', "wb", "utf-8-sig")

stop_times = []


for line in stop_timesFile:
    words = line.split(",")
    if words[1] not in stop_times:
        print words[1]
        stop_times.append(words[1])

for line in inputFile:
    words = line.split(",")
    if words[0] in stop_times:
        print words[0]+','+words[1]+','+words[2]+','+words[3]
        outputFile.write(words[0]+','+words[1]+','+words[2]+','+words[3])


stop_timesFile.close()
outputFile.close()
inputFile.close()
print "end stops"
'''
#-------end stops-------



#-------split new_stop_times-------
'''
print "split new_stop_times"

inputFile = codecs.open('./resources/JER_TLV/stop_times_JER_TLV.txt', "r", "utf-8-sig")
readLine = inputFile.readlines()
for i in range(0,22):
    outputFile = codecs.open('./resources/JER_TLV/stop_times_directory/stop_times_JER_TLV'+str(i)+'.txt', "wb", "utf-8-sig")
    for j in range(((i * 40000) + 1), ((i+1) * 40000)+1):
        print j
        outputFile.write(readLine[j-1])

print "end split new_stop_times"
'''
#-------end split new_stop_times-------