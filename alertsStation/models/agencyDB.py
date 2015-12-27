from google.appengine.ext import ndb
import json

class Agency(ndb.Model):
    agency_id=ndb.StringProperty()
    agency_name=ndb.StringProperty()

    @staticmethod
    def getAllAgency():
        agency=Agency.query()
        list=[]

        for res in agency:
            tempAgemcy={}
            tempAgemcy['agencyId']=res.agency_id
            tempAgemcy['agencyName']=res.agency_name
            list.append(tempAgemcy)

        reply_json=json.dumps(list,ensure_ascii=False)

        return reply_json




