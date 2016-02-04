from google.appengine.ext import ndb
import json
import codecs

class Agency(ndb.Model):
    agency_id=ndb.IntegerProperty()
    agency_name=ndb.StringProperty()

    @staticmethod
    def readFromGtfsAgency():
        fo = codecs.open('./resources/JER/agency_JER.txt', "r", "utf-8-sig")
        for line in fo:
           words = line.split(",")
           agID=words[0]
           agID=agID.strip()
           agID=int(agID)

           agName=words[1]
           addRow=Agency(agency_id=agID,agency_name=agName)
           addRow.put()
        fo.close()

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
    @staticmethod
    def test(num):
        q=Agency.query(Agency.agency_id==num)
        for res in q:
            if res is not None:
                return res.agency_name
    @staticmethod
    def test2(ag_name):
        res=Agency.query(Agency.agency_name==ag_name)
        for res2 in res:
            if res2 is not None:
                return res2.agency_id

        return "no result"




