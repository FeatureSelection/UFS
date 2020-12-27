# Plotting algorithm running times from

# A systematic evaluation of filter Unsupervised Feature Selection methods
# SaúlSolorio-Fernández J.Ariel Carrasco-OchoaJosé Fco.Martínez-Trinidad

library(data.table)
library(ggplot2)

# Tables 19 and 20
dtr <- read.csv("/home/venus/Downloads/DSUFS75-time.csv",sep=',')

dtr <- as.data.table(dtr)

# Tables 1 and 2
dtz <- read.table("/home/venus/Downloads/DSUFS75-size.csv",sep=',',header=TRUE)
dtz <- as.data.table(dtz)

# Converting from wide to long format
ldtr <- melt(dtr, id.vars = c("Dataset"), variable.name = "method",value.name = "rtime")

setkey(ldtr,Dataset)
setkey(dtz,Dataset)

# Merging tables
# https://rstudio-pubs-static.s3.amazonaws.com/52230_5ae0d25125b544caab32f75f0360e775.html
ldtr <- dtz[ldtr]

# http://zevross.com/blog/2019/04/02/easy-multi-panel-plots-in-r-using-facet_wrap-and-facet_grid-from-ggplot2/
p <- ggplot(ldtr,aes(x=N_of_Features,y=rtime)) + geom_point() + 
  stat_smooth(method=lm,se=F,lwd=1.2,fullrange=T)+theme_bw() +
  facet_wrap(~method)

# Vertical rotation of x axis text
p <- p + theme(axis.text.x = element_text(angle = 45))
p
