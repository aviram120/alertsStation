from google.appengine.ext import ndb
import logging

class Tb_file(ndb.Model):
    file_name = ndb.IntegerProperty()
    is_read = ndb.IntegerProperty()
   

    @staticmethod
    def putDataToModel(fileNumber):
        for i in range(fileNumber):
            addRow=Tb_file(file_name=i, is_read=0)
            addRow.put()
        return "add all file to db"

    @staticmethod
    def getIndexFileToRead():
        tb_file_loc = Tb_file.query()
        for res in tb_file_loc:
            if (res.is_read==0):
                updataRow=Tb_file(file_name=res.file_name, is_read=1)
                updataRow.put()
                res.key.delete()
                file_name = res.is_read
                return file_name
        return -1