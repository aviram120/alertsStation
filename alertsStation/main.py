from models.agencyDB import Agency
from models.RoutesDB import Routes
from models.TripsDB import Trips
from models.StopTimeDB import StopTime
from models.StopsDB import Stops
import logging
import webapp2
#http://alertsstation-1172.appspot.com/agency?id=2
class MainHandler(webapp2.RequestHandler):
    def get(self):#agency?id=1
        id=self.request.get('id')
        num=self.request.get('num')

        if (id!=""):
            id=id.strip()
            id=int(id)
            if (id==1):#read data from file to DB
                fo = open("agency.txt", "r")
                for line in fo:
                   words = line.split(",")
                   agID=words[0]
                   agID=agID.strip()
                   agID=int(agID)

                   agName=words[1]
                   addRow=Agency(agency_id=agID,agency_name=agName)
                   addRow.put()
                fo.close()
                self.post("add to DB all file")
            if (id==2):#get all DB
                agency=Agency.getAllAgency()
                self.post(agency)
            if (id == 3):#read data from file to DB
                route = Routes.readFromGtfsRoutes()
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
