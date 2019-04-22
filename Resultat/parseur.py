import xlwt

book = xlwt.Workbook()
feuil1 = book.add_sheet('feuille 1')

feuil1.write(0,1,'x')
feuil1.write(0,2,'y')
feuil1.write(0,3,'time mobile 1')

feuil1.write(0,5,'x')
feuil1.write(0,6,'y')
feuil1.write(0,7,'time mobile 2')

fichier = open("test.txt", "r")
contenu = fichier.read()
contenu = contenu.split("\n")
i = 1
j = 1
for count, val in enumerate(contenu):
    val = val.split(" ");
    print(val[1])
    if(int(val[1]) == 0):
        feuil1.write(i, 1,val[3])
        feuil1.write(i, 2,val[5])
        feuil1.write(i, 3,val[7])
        i = i+1
    elif(int(val[1]) == 1):
        feuil1.write(j, 5,val[3])
        feuil1.write(j, 6,val[5])
        feuil1.write(j, 7,val[7])
        j = j+1

book.save('experience.xls')
