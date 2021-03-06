from models.agencyDB import Agency
from models.RoutesDB import Routes
from models.TripsDB import Trips
from models.StopTimeDB import StopTime
from models.StopsDB import Stops
from models.TbFileDB import Tb_file
import logging
import webapp2
from google.appengine.ext import ndb

#http://alertsstation-1172.appspot.com/api?id=2
class MainHandler(webapp2.RequestHandler):
    def get(self):#agency?id=1
        id=self.request.get('id')
        id_stop_time=self.request.get('id_stop_time')
        num=self.request.get('num')

        act=self.request.get('act')
        agency_id=self.request.get('agency_id')
        city_id=self.request.get('city_id')
        route_id=self.request.get('route_id')


        if (act!=""):
            act=act.strip()
            act=int(act)


            #/api?act=1
            if (act==1):#get all agency
                allAgency=Agency.getAllAgency()
                self.post(allAgency)

            #/api?act=2&agency_id=
            elif (act==2):#get all city for agency
                if (agency_id!=""):
                    agency_id=agency_id.strip()
                    agency_id=int(agency_id)

                    cityByAgencyID=Routes.getAllCityByAgencyID(agency_id)
                    self.post(cityByAgencyID)

            #/api?act=3&agency_id=xxx&city_name=xxx
            elif (act==3):#get route_shirt_name, route_id
                if (agency_id!=""):
                    agency_id=agency_id.strip()
                    agency_id=int(agency_id)
                    if (city_id!=""):
                        city_id=city_id.strip()
                        city_id=int(city_id)
                        routesIdName=Routes.getAllRoutesByAgencyCity(agency_id,city_id)
                        self.post(routesIdName)

            #/api?act=4&route_id=xxx
            elif (act==4):#get stop_name,stop_lan,stop_lon,stop_sequnce
                if (route_id!=""):
                    route_id=route_id.strip()
                    route_id=int(route_id)
                    stopsLocation=Trips.getAllStpsByRoutID(route_id)
                    self.post(stopsLocation)


        if (id!=""):
            id=id.strip()
            id=int(id)
            if (id==1):#read data from file to DB
                agency=Agency.readFromGtfsAgency()
                self.post("read from file agency")
            if (id==2):#get all DB
                agency=Agency.getAllAgency()
                self.post(agency)
            if (id == 3):#read data from file to DB
                route = Routes.readFromGtfsRoutes()
                '''
                #delete all Routes
                routes_keys = Routes.query().fetch(keys_only=True)
                ndb.delete_multi(routes_keys)
                '''
                self.post("read from file route")
            if (id == 4):#get all DB
                route = Routes.getAllRoutes()
                self.post(route)
            if (id == 5):#read data from file to DB
                trips = Trips.readFromGtfsTrips()
                self.post("read from file trips")
            if (id == 6):#get all DB
                trips = Trips.getAllTrips()
                self.post(trips)
            if (id == 7):#read data from file to DB
                stops = Stops.readFromGtfsStops()
                self.post("read from file stops")
            if (id == 8):#get all DB
                stops = Stops.getAllStops()
                self.post(stops)
            if (id == 9):#read data from file to DB
                if(id_stop_time != ""):
                    id_stop_time=id_stop_time.strip()
                    id_stop_time=int(id_stop_time)
                    stopTime = StopTime.readFromGtfsStopTime(id_stop_time)
                    self.post("read from file stopTime")
            if (id == 10):#get all DB
                stopTime = StopTime.getAllStopTime()
                self.post(stopTime)
            if (id == 11):#get all DB
                num=num.strip()
                num=int(num)
                self.post(Agency.test(num))
            if (id == 12):#get all DB
                tbFile = Tb_file.putDataToModel(48)
                self.post(tbFile)
            if (id == 13):#delete all stoptime
                StopTime.deleteAllStopTime()
                self.post("delete all stopTime")

    def post(self,response):
        self.response.write(response)

app = webapp2.WSGIApplication([
    ('/api', MainHandler)
], debug=True)
