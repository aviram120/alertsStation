from models.agencyDB import Agency
import webapp2

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
                   agName=words[1]
                   addRow=Agency(agency_id=agID,agency_name=agName)
                   addRow.put()
                fo.close()
                self.post("add to DB all file")
            if (id==2):#get all DB
                agency=Agency.getAllAgency()
                self.post(agency)


    def post(self,response):
        self.response.write(response)

app = webapp2.WSGIApplication([
    ('/agency', MainHandler)
], debug=True)
