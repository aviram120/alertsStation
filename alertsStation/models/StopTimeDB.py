from google.appengine.ext import ndb
import json
import codecs
import thread
import threading
from threading import Lock
import json  #use for create json
import logging


mutex = Lock()

class StopTime(ndb.Model):
    trip_id = ndb.StringProperty()
    stop_id = ndb.IntegerProperty()
    stop_sequence = ndb.IntegerProperty()


    @staticmethod
    def readFromGtfsStopTime(file_number):
        fo = codecs.open('./resources/JER/stop_times_directory/stop_times_JER'+str(file_number)+'.txt', "r", "utf-8-sig")
        for line in fo:
            words = line.split(",")

            trip_id_loc = words[0]

            stop_id_loc = words[1]
            stop_id_loc = stop_id_loc.strip()
            stop_id_loc = int(stop_id_loc)

            stop_sequence_loc = words[2]
            stop_sequence_loc = stop_sequence_loc.strip()
            stop_sequence_loc = int(stop_sequence_loc)


            addRow = StopTime(trip_id = trip_id_loc, stop_id = stop_id_loc, stop_sequence = stop_sequence_loc)
            addRow.put()


        fo.close()

    '''
    def readFromGtfsStopTime():

        logging.info("in readFromGtfsStopTime")
        for i in range(48):
            logging.info(i)
            thread.start_new_thread(multiRequests,(i,))

    def multiRequests(self,file_num):
        logging.info(file_num)
        fo = codecs.open('./resources/stop_times_directory/stop_times_'+str(file_num)+'.txt', "r", "utf-8-sig")
        for line in fo:
            words = line.split(",")

            trip_id_loc = words[0]
            trip_id_loc = trip_id_loc.strip()
            trip_id_loc = int(trip_id_loc)

            stop_id_loc = words[1]
            stop_id_loc = stop_id_loc.strip()
            stop_id_loc = int(stop_id_loc)

            stop_sequence_loc = words[2]
            stop_sequence_loc = stop_sequence_loc.strip()
            stop_sequence_loc = int(stop_sequence_loc)


            addRow = StopTime(trip_id = trip_id_loc, stop_id = stop_id_loc, stop_sequence = stop_sequence_loc)
            mutex.acquire()
            addRow.put()
            mutex.release()

        fo.close()



        fo = codecs.open('./resources/new_stop_times.txt', "r", "utf-8-sig")
        for line in fo:
            words = line.split(",")

            trip_id_loc = words[0]
            trip_id_loc = trip_id_loc.strip()
            trip_id_loc = int(trip_id_loc)

            stop_id_loc = words[1]
            stop_id_loc = stop_id_loc.strip()
            stop_id_loc = int(stop_id_loc)

            stop_sequence_loc = words[2]
            stop_sequence_loc = stop_sequence_loc.strip()
            stop_sequence_loc = int(stop_sequence_loc)


            addRow = StopTime(trip_id = trip_id_loc, stop_id = stop_id_loc, stop_sequence = stop_sequence_loc)
            addRow.put()

        fo.close()
        '''



    @staticmethod
    def getAllStopTime():
        stopTime=StopTime.query()
        list=[]

        for res in stopTime:
            tempStopTime = {}
            tempStopTime['trip_id']=res.trip_id
            tempStopTime['stop_id']=res.stop_id
            tempStopTime['stop_sequence']=res.stop_sequence
            list.append(tempStopTime)

        reply_json=json.dumps(list,ensure_ascii=False)

        return reply_json



    @staticmethod
    def deleteAllStopTime():
        ndb.delete_multi(StopTime.query().fetch(keys_only=True))
