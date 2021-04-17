library(ISLR)
library(xtable)
rm(list = ls())

# Set variable
Training_Set_Size = 250
Test_Set_Size = 260 - Training_Set_Size

# Make sure input is valid
if (Test_Set_Size <= 130 && 260 %% Test_Set_Size != 0)
  stop

if (Training_Set_Size < 130 && 260 %% Training_Set_Size != 0)
  stop

Number_Of_iterations = 260 / min(Test_Set_Size, Training_Set_Size)

# Collect data and omit all cases that are incomplete (i.e. that have an "NA")
# Delete some data rows so that we can easily apply 10-fold Cross-Validation
Hitters = na.omit(Hitters)
Hitters = Hitters[1:260,]

# Make vector that will store the mean sum of squared errors
MSE = rep(0 , Number_Of_iterations)

for(k in 0:(Number_Of_iterations - 1)) {
  # Select training and test set
  if (Training_Set_Size > Test_Set_Size) {
    testset = (k*Test_Set_Size):((k+1)*Test_Set_Size-1)
    training = 1:260
    training = training[!training %in% testset] 
  } else {
    training = (k*Training_Set_Size):((k+1)*Training_Set_Size-1)
    testset = 1:260
    testset = testset[!testset %in% training]
  }
  
  # Store the selected training data into a data matrix X and target vector y
  X = model.matrix(Salary~., Hitters)[,-1][c(training),c(2,4,6,7)]
  y = Hitters$Salary[training]
  
  # Apply regular regression.
  m = lm(y ~ X)
  
  # Store the regression coefficients in a vector
  beta = coef(m)
  
  # Test the model with the test set
  X_test = model.matrix(Salary~., Hitters)[c(testset),c(1,3,5,7,8)]
  y_test = Hitters$Salary[testset]
  y_predict = X_test %*% beta
  
  # Calculate the mean sum of squared errors
  MSE[k+1] = (1/Test_Set_Size)*sum((y_test - y_predict)^2)
}

mean(MSE)