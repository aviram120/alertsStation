from models.agencyDB import Agency
from models.RoutesDB import Routes
from models.TripsDB import Trips
from models.StopTimeDB import StopTime
from models.StopsDB import Stops
import logging
import webapp2
from google.appengine.ext import ndb

#http://alertsstation-1172.appspot.com/api?id=2
class MainHandler(webapp2.RequestHandler):
    def get(self):#agency?id=1
        id=self.request.get('id')
        num=self.request.get('num')

        act=self.request.get('act')
        agency_id=self.request.get('agency_id')
        city_name=self.request.get('city_name')
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
                    if (city_name!=""):
                        #TODO
                        return

            #/api?act=4&route_id=xxx
            elif (act==4):#get stop_name,stop_lan,stop_lon,stop_sequnce
                if (route_id!=""):
                    route_id=route_id.strip()
                    route_id=int(route_id)
                    #TODO
                    return








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
                stopTime = StopTime.readFromGtfsStopTime()
                self.post("read from file stopTime")
            if (id == 10):#get all DB
                stopTime = StopTime.getAllStopTime()
                self.post(stopTime)
            if (id == 11):#get all DB
                num=num.strip()
                num=int(num)
                self.post(Agency.test(num))


    def post(self,response):
        self.response.write(response)

app = webapp2.WSGIApplication([
    ('/api', MainHandler)
], debug=True)
