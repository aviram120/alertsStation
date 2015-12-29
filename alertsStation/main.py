from models.agencyDB import Agency
from models.RoutesDB import Routes
from models.TripsDB import Trips
from models.StopTimeDB import StopTime
from models.StopsDB import Stops

import webapp2
#http://alertsstation-1172.appspot.com/agency?id=2
class MainHandler(webapp2.RequestHandler):
    def get(self):#agency?id=1
        id=self.request.get('id')
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
            if (id == 4):#read data from file to DB
                route = Routes.getAllRoutes()
                self.post(route)
            if (id == 5):#read data from file to DB
                trips = Trips.readFromGtfsTrips()
                self.post("read from file trips")
            if (id == 6):#read data from file to DB
                trips = Trips.getAllTrips()
                self.post(trips)
            if (id == 7):#read data from file to DB
                stops = Stops.readFromGtfsStops()
                self.post("read from file stops")
            if (id == 8):#read data from file to DB
                stops = Stops.getAllStops()
                self.post(stops)

    def post(self,response):
        self.response.write(response)

app = webapp2.WSGIApplication([
    ('/agency', MainHandler)
], debug=True)
