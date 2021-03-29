library(ISLR)
library(xtable)
library(glmnet)
rm(list = ls())

# Set variable
Training_Set_Size = 10
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
MSSE = rep(0 , Number_Of_iterations)

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
  
  # Create a set of values for the tuning parameter lambda used later in the
  # gmlnet function.
  lambdas = 10^seq(5, -5, by = -.1)
  
  # Store the selected training data into a data matrix X and target vector y
  X = model.matrix(Salary~., Hitters)[,-1][c(training),c(2,4,6,7)]
  y = Hitters$Salary[training]
  
  # Apply ridge regression on the data. Below we create a model based on 4
  # predictors for the variable 'Salary'. Note that the best lambda is auto-
  # matically selected by the function cv.glmnet()
  cv_fit = cv.glmnet(X, y, alpha = 0, lambda = lambdas, intercept = TRUE, standardize = FALSE)
  lambda_best = cv_fit$lambda.min
  
  ridge_model <- glmnet(X, y, alpha = 0, lambda = lambda_best, 
                        intercept = TRUE, standardize = TRUE)
  
  # Store the regression coefficients in a vector
  beta = coef(cv_fit, s = lambda_best)
  
  # Test the model with the test set
  X_test = model.matrix(Salary~., Hitters)[c(testset),c(3,5,7,8)]
  y_test = Hitters$Salary[testset]
  y_predict = predict(cv_fit, X_test, s = lambda_best)
  
  # MSSE = Mean Sum of Squared Errors
  MSSE[k+1] = (1/Test_Set_Size)*sum((y_test - y_predict)^2)
}

mean(MSSE)

# -----------------------------------------------------------------------------
# -------------------- Plotting the MSE as lambda varies ----------------------
# -----------------------------------------------------------------------------
# We plot the MSE with the mean MSE of ridge regression for different values of
# lambda with the MSE of Least Squares Regression.
rm(list = ls())

# Collect data and omit all cases that are incomplete (i.e. that have an "NA")
Hitters = na.omit(Hitters)
Hitters = Hitters[1:260,]

# Initialize the necessary vectors. For simplicity, we will not use cross-
# validation. Instead we keep using the same training and test sets.
Training_Set_Size = 10

lambdas = 10^seq(5, -5, by = -.1)
betas = matrix(0L, 5, length(lambdas))
MSE = rep(0 , length(lambdas))
training = 1:Training_Set_Size
testset = (Training_Set_Size + 1):260

# Store the selected training data into a data matrix X and target vector y
X = model.matrix(Salary~., Hitters)[,-1][c(training),c(2,4,6,7)]
y = Hitters$Salary[training]

# Store the selected test data into a data matrix X and target vector y
X_test = model.matrix(Salary~., Hitters)[c(testset),c(3,5,7,8)]
y_test = Hitters$Salary[testset]

# Loop over all lambdas, construct the model and store the MSE and coefficients.
for (i in 1:length(lambdas)) {
  lambda = lambdas[i]
  
  # Apply ridge regression on the data. Below we create a model based on 4
  # predictors for the variable 'Salary'.
  ridge_model <- glmnet(X, y, alpha = 0, lambda = lambda, 
                        intercept = TRUE, standardize = TRUE)
  
  # Store the regression coefficients in a vector
  betas[,i] = coef(ridge_model)@x
  
  # Test the model with the test set
  y_predict = predict(ridge_model, X_test, s = lambda)
    
  # MSSE = Mean Sum of Squared Errors
  MSE[i] = (1/length(testset))*sum((y_test - y_predict)^2)
}

# For reference, also construct the Least Squares model and calculate the MSE.
LS_model <- lm(y ~ X)
beta_LS = coef(LS_model)
X_test_LS = model.matrix(Salary~., Hitters)[c(testset),c(1,3,5,7,8)]
y_predict_LS = X_test_LS %*% beta_LS
LS_MSE = (1/length(testset))*sum((y_test - y_predict_LS)^2)

# Plot the MSE for different values of lambda
best_MSE_index = which(MSE == min(MSE))
plot(log(lambdas), MSE, "l", main = "A plot of the MSE (Ridge) in function of lambda")
lines(log(lambdas), rep(LS_MSE, length(lambdas)), "l", col ='red', lty = 3)
points(x = log(lambdas[best_MSE_index]), y = MSE[best_MSE_index], pch = 25,
       col = 'blue', cex = 1, bg = 'blue')

# Plot the coefficients of the ridge model with respect to lambda
cl <- rainbow(5)
plot(log(lambdas), betas[2,], "l", col = cl[2], ylim = c(-5,7), 
     ylab = "Coefficients", main = "A plot of the coefficients (Ridge) in function of lambda")
lines(log(lambdas), rep(0, length(lambdas)), lty = 3)
for(i in 2:5) {
  lines(log(lambdas), betas[i,], "l", col = cl[i])
}



