library(ISLR)
library(xtable)
rm(list = ls())

# Collect data and omit all cases that are incomplete (i.e. that have an "NA")
Hitters = na.omit(Hitters)

# Apply linear regression on the data. Below we create a model based on 4
# predictors for the variable 'Salary'.
m = lm(Salary ~ Hits + Years + Walks + Runs, data = Hitters)

# Store a vector of the standard deviations of the regression coefficients
sd = summary(m)$coef[,2]

# Now systematically omit almost all data, making n approximately equal to p
# (but still n > p)
Hitters = Hitters[1:100,]
m = lm(Salary ~ Hits + Years + Walks + Runs, data = Hitters)
sd = cbind(sd, summary(m)$coef[,2])

Hitters = Hitters[1:75,]
m = lm(Salary ~ Hits + Years + Walks + Runs, data = Hitters)
summary(m)$coef[,2]
sd = cbind(sd, summary(m)$coef[,2])

Hitters = Hitters[1:50,]
m = lm(Salary ~ Hits + Years + Walks + Runs, data = Hitters)
summary(m)$coef[,2]
sd = cbind(sd, summary(m)$coef[,2])

Hitters = Hitters[1:25,]
m = lm(Salary ~ Hits + Years + Walks + Runs, data = Hitters)
sd = cbind(sd, summary(m)$coef[,2])

Hitters = Hitters[1:6,]
m = lm(Salary ~ Hits + Years + Walks + Runs, data = Hitters)
sd = cbind(sd, summary(m)$coef[,2])
summary(m)

xtable(sd)
