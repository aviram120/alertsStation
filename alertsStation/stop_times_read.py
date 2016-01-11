import logging
import webapp2
from models.StopTimeDB import StopTime
from models.TbFileDB import Tb_file


class Stop_time_read(webapp2.RequestHandler):
    def get(self):
        logging.info('from stop_time_read cron job')
        number_file = Tb_file.getIndexFileToRead()
        logging.info('number file = ' + str(number_file))
        if number_file != -1:
            StopTime.readFromGtfsStopTime(number_file)

app = webapp2.WSGIApplication([
    ('/stop_times_cron', Stop_time_read)
], debug=True)