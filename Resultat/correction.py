import xlrd
import math
import  xlwt

wb = xlrd.open_workbook('experience1.xls')
sh = wb.sheet_by_name(u'feuille 1')
colonnex1 = sh.col_values(1)
colonney1 = sh.col_values(2)
time = sh.col_values(3)
colonnex2 = sh.col_values(5)
colonney2 = sh.col_values(6)

book = xlwt.Workbook()
feuil1 = book.add_sheet('feuille 1')

feuil1.write(0,1,'x')
feuil1.write(0,2,'y')
feuil1.write(0,3,'time mobile 1')
feuil1.write(0,5,'x')
feuil1.write(0,6,'y')
feuil1.write(0,7,'time mobile 2')

tnpx1 = 0;
tnpx2 = 0;

count = 1
for el in colonnex1:
    if(abs(tnpx1-int(colonnex1[count])) > abs(tnpx2-int(colonnex2[count]))):
        feuil1.write(count, 1,colonnex2[count])
        feuil1.write(count, 2,colonney2[count])
        feuil1.write(count, 3,time[count])
        feuil1.write(count, 5,colonney1[count])
        feuil1.write(count, 6,colonney1[count])
        feuil1.write(count, 7,time[count])
    else:
        feuil1.write(count, 1,colonnex1[count])
        feuil1.write(count, 2,colonney1[count])
        feuil1.write(count, 3,time[count])
        feuil1.write(count, 5,colonney2[count])
        feuil1.write(count, 6,colonney2[count])
        feuil1.write(count, 7,time[cou2t])
    tnpx1 = colonnex1[count]
    tnpx2 = colonnex2[count]
book.save('experience.xls')
