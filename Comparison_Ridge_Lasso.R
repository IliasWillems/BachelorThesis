# The goal of this R-script is to compare the ridge and Lasso estimators. It is
# extensively used in Chapter 5 of our paper. The data set for this example
# was taken from Kaggle.com

# Load required packages, clear workspace and increase memory limit as we will
# be working with large matrices.
library(glmnet)
library(readr)
library(data.table)
library(Matrix)
rm(list = ls())
memory.limit(size = 20000)

# Read the data and omit all cases that are incomplete (i.e. that have an "NA"
# in one or more columns).
epi_r = read_csv(",School/Bachelorproef/Data/epi_r.csv")
epi_r$title = NULL
epi_r = na.omit(epi_r)
epi_r = epi_r[1:15000,]

# Rename some badly named columns
names(epi_r)[names(epi_r) == "#cakeweek"] <- "HashTag_Cakeweek"
names(epi_r)[names(epi_r) == "#wasteless"] <- "HashTag_Wasteless"
names(epi_r)[64] <- "Bon Appetit"
names(epi_r)[168] <- "creme_de_cacao2"

# An overview of what is done below:

# 0. Regular regression (fails)
# 1. Lasso
# 2. Lasso with altered column
# 3. Comparison of ridge and Lasso
# 4. Investigate

# -----------------------------------------------------------------------------
# -------------- 0. Perform regular regression. (It should fail) --------------
# -----------------------------------------------------------------------------

# Initialize necessary variables. Since running linear regression on a data set
# this large is time consuming, we choose the training and test sets in such a
# way so that we only need to apply linear regression three times.
Training_Set_Size = 10000
Test_Set_Size = 15000 - Training_Set_Size
Number_Of_iterations = 15000 / Test_Set_Size

# Make vector that will store the mean sum of squared errors
LS_MSE = rep(0 , Number_Of_iterations)

for(k in 0:Number_Of_iterations - 1) {
  # Select training and test set
  testset = (k*Test_Set_Size):((k+1)*Test_Set_Size-1)
  training = 1:15000
  training = training[!training %in% testset] 
  
  # Store the selected training data into a data matrix X and target vector y.
  # Do the same for the test data.
  X_training = model.matrix(rating~., epi_r)[,-1][c(training),]
  y_training = epi_r$rating[training]
  X_test = model.matrix(rating~., epi_r)[c(testset),]
  y_test = epi_r$rating[testset]
  
  # Apply regular regression.
  m = lm(y_training ~ X_training)
  
  # Store the regression coefficients in a vector
  beta = coef(m)
  
  # Test the model with the test set
  y_predict = X_test %*% beta
  
  # Calculate the mean sum of squared errors
  LS_MSE[k+1] = (1/Test_Set_Size)*sum((y_test - y_predict)^2)
}

LSS_cv_MSE = mean(LS_MSE)
# The result is 'NA'. This is because some coefficients from the Least Squares
# model are 'NA'. This could be due to column being linearly dependent. Hence,
# we cannot use linear regression for this data set.

# -----------------------------------------------------------------------------
# ------------ 1. Perform Lasso to see which predictors it selects ------------
# -----------------------------------------------------------------------------
X_training = model.matrix(rating~., epi_r)[,-1]
y_training = epi_r$rating

# Find the best choice for lambda
lambdas = 10^seq(5, -5, by = -.1)
best_lasso_model = cv.glmnet(X_training, y_training, alpha = 1,
                             intercept = TRUE, standardize = TRUE,
                             lambda = lambdas)
best_lambda = best_lasso_model$lambda.min
best_lasso_model = glmnet(X_training, y_training, alpha = 1,
                          intercept = TRUE, standardize = TRUE,
                          lambda = best_lambda)

# Find the indices of the 10 largest coefficients
coeff = predict(best_lasso_model, type = "coefficients", s = best_lambda)
sorted_coeff = sort(coeff, decreasing = TRUE)

smallest = 679
highest = 1
highest_values = rep(0, 11)
for(i in 1:11) {
  if (abs(sorted_coeff[smallest]) > sorted_coeff[highest]) {
    highest_values[i] = sorted_coeff[smallest]
    smallest = smallest - 1
  } else {
    highest_values[i] = sorted_coeff[highest]
    highest = highest + 1
  }
}
most_important = which(coeff[1:679] %in% highest_values)

names(epi_r[most_important])

# -----------------------------------------------------------------------------
# - 2. Set the value for field '22-minute meals' of all dished that have high - 
# ----- rating to 1 and run Lasso. It will now also select this predictor. ----
# -----------------------------------------------------------------------------

epi_r$`22-minute meals`[epi_r$rating > 3] = 1

X_training = model.matrix(rating~., epi_r)[,-1]
y_training = epi_r$rating

# Find the best choice for lambda
lambdas = 10^seq(5, -5, by = -.1)
best_lasso_model = cv.glmnet(X_training, y_training, alpha = 1,
                             intercept = TRUE, standardize = TRUE,
                             lambda = lambdas)
best_lambda = cv_lasso_model$lambda.min
best_lasso_model = glmnet(X_training, y_training, alpha = 1,
                          intercept = TRUE, standardize = TRUE,
                          lambda = best_lambda)

# Find the indices of the 10 largest coefficients
coeff = predict(best_lasso_model, type = "coefficients", s = best_lambda)
sorted_coeff = sort(coeff, decreasing = TRUE)

smallest = 679
highest = 1
highest_values = rep(0, 11)
for(i in 1:11) {
  if (abs(sorted_coeff[smallest]) > sorted_coeff[highest]) {
    highest_values[i] = sorted_coeff[smallest]
    smallest = smallest - 1
  } else {
    highest_values[i] = sorted_coeff[highest]
    highest = highest + 1
  }
}
highest_values = highest_values[!highest_values == 0]
most_important = which(coeff[1:679] %in% highest_values)[]

names(epi_r[most_important])

# -----------------------------------------------------------------------------
# ------------------------ 3.1 Apply ridge regression. ------------------------
# -----------------------------------------------------------------------------

# We first read in the data again because we altered it in the previous section,
# making the same modifications to it as before, with the exception that we
# immediately restrict the data to its first 700 rows.
rm(list = ls())

epi_r = read_csv(",School/Bachelorproef/Data/epi_r.csv")
epi_r$title = NULL
epi_r = na.omit(epi_r)
epi_r = epi_r[1:15000,]

names(epi_r)[names(epi_r) == "#cakeweek"] <- "HashTag_Cakeweek"
names(epi_r)[names(epi_r) == "#wasteless"] <- "HashTag_Wasteless"
names(epi_r)[64] <- "Bon Appetit"
names(epi_r)[168] <- "creme_de_cacao2"

# Apply ridge regression (code is mostly copied form
# 'Example_ridge_n_approx_pV2.R'). This section takes a while to run
Training_Set_Size = 10000
Test_Set_Size = 15000 - Training_Set_Size
Number_Of_iterations = 15000 / Test_Set_Size

ridge_MSE = rep(0 , Number_Of_iterations)

for(k in 0:(Number_Of_iterations - 1)) {
  # Select training and test set
  testset = (k*Test_Set_Size):((k+1)*Test_Set_Size-1)
  training = 1:15000
  training = training[!training %in% testset]
  
  # Store the selected training data into a data matrix X and target vector y
  X = model.matrix(rating~., epi_r)[,-1][c(training),]
  y = epi_r$rating[training]
  
  # Create a set of values for the tuning parameter lambda used later in the
  # gmlnet function.
  lambdas = 10^seq(5, -5, by = -.1)
  
  # Apply ridge regression on the data.
  cv_fit = cv.glmnet(X, y, alpha = 0, lambda = lambdas, intercept = TRUE,
                     standardize = FALSE)
  lambda_best = cv_fit$lambda.min
  
  ridge_model <- glmnet(X, y, alpha = 0, lambda = lambda_best, 
                        intercept = TRUE, standardize = TRUE)
  
  # Test the model with the test set
  X_test = model.matrix(rating~., epi_r)[c(testset),-1]
  y_test = epi_r$rating[testset]
  y_predict = predict(ridge_model, X_test, s = lambda_best)
  
  # MSSE = Mean Sum of Squared Errors
  ridge_MSE[k+1] = (1/Test_Set_Size)*sum((y_test - y_predict)^2)
}

Ridge_cv_MSE = mean(ridge_MSE)

# -----------------------------------------------------------------------------
# ----------------- 3.2 Plotting the MSE as lambda varies ---------------------
# -----------------------------------------------------------------------------

# Initialize the necessary vectors. For simplicity, we will not use cross-
# validation. Instead we keep using the same training and test sets.
Training_Set_Size = 10000

lambdas = 10^seq(5, -5, by = -.1)
ridge_betas = matrix(0L, 679, length(lambdas))
ridge_MSE = rep(0 , length(lambdas))
training = 1:Training_Set_Size
testset = (Training_Set_Size + 1):15000

# Store the selected training data into a data matrix X and target vector y
X = model.matrix(rating~., epi_r)[,-1][c(training),]
y = epi_r$rating[training]

# Store the selected test data into a data matrix X and target vector y
X_test = model.matrix(rating~., epi_r)[c(testset),-1]
y_test = epi_r$rating[testset]

# Loop over all lambdas, construct the model and store the MSE and coefficients.
for (i in 1:length(lambdas)) {
  lambda = lambdas[i]
  
  # Apply ridge regression on the data. Below we create a model based on 4
  # predictors for the variable 'Salary'.
  ridge_model <- glmnet(X, y, alpha = 0, lambda = lambda, 
                        intercept = TRUE, standardize = TRUE)
  
  # Store the regression coefficients in a vector
  ridge_betas[,i] = predict(ridge_model, type = "coefficients", s = lambda)[1:679]
  
  # Test the model with the test set
  y_predict = predict(ridge_model, X_test, s = lambda)
  
  # MSSE = Mean Sum of Squared Errors
  ridge_MSE[i] = (1/length(testset))*sum((y_test - y_predict)^2)
}

# For reference, also construct the Least Squares model and calculate the MSE.
LS_model <- lm(y ~ X)
beta_LS = coef(LS_model)
X_test_LS = model.matrix(rating~., epi_r)[c(testset),]
y_predict_LS = X_test_LS %*% beta_LS
LS_MSE = (1/length(testset))*sum((y_test - y_predict_LS)^2)

# Plot the MSE for different values of lambda
best_MSE_index = which(ridge_MSE == min(ridge_MSE))
plot(log(lambdas), ridge_MSE, "l",
     main = "A plot of the MSE (Ridge) in function of lambda")
lines(log(lambdas), rep(LS_MSE, length(lambdas)), "l", col ='red', lty = 3)
points(x = log(lambdas[best_MSE_index]), y = ridge_MSE[best_MSE_index], pch = 25,
       col = 'blue', cex = 1, bg = 'blue')

# Plot the coefficients of the ridge model with respect to lambda
cl <- rainbow(679)
plot(log(lambdas), ridge_betas[2,], "l", col = cl[2], ylim = c(-5,5), 
     ylab = "Coefficients",
     main = "A plot of the coefficients (Ridge) in function of lambda")
lines(log(lambdas), rep(0, length(lambdas)), lty = 3)
for(i in 2:679) {
  lines(log(lambdas), ridge_betas[i,], "l", col = cl[i])
}

# -----------------------------------------------------------------------------
# --------------------------- 3.3 Apply the lasso -----------------------------
# -----------------------------------------------------------------------------

# Apply the lasso (code is mostly copied form
# 'Example_lasso_n_approx_pV2.R'). This section takes a while to run

lasso_MSE = rep(0 , Number_Of_iterations)

for(k in 0:(Number_Of_iterations - 1)) {
  # Select training and test set
  testset = (k*Test_Set_Size):((k+1)*Test_Set_Size-1)
  training = 1:15000
  training = training[!training %in% testset]
  
  # Store the selected training data into a data matrix X and target vector y
  X = model.matrix(rating~., epi_r)[,-1][c(training),]
  y = epi_r$rating[training]
  
  # Create a set of values for the tuning parameter lambda used later in the
  # gmlnet function.
  lambdas = 10^seq(5, -5, by = -.1)
  
  # Apply ridge regression on the data.
  cv_fit = cv.glmnet(X, y, alpha = 1, lambda = lambdas, intercept = TRUE,
                     standardize = FALSE)
  lambda_best = cv_fit$lambda.min
  
  lasso_model <- glmnet(X, y, alpha = 1, lambda = lambda_best, 
                        intercept = TRUE, standardize = TRUE)
  
  # Test the model with the test set
  X_test = model.matrix(rating~., epi_r)[c(testset),-1]
  y_test = epi_r$rating[testset]
  y_predict = predict(ridge_model, X_test, s = lambda_best)
  
  # MSSE = Mean Sum of Squared Errors
  lasso_MSE[k+1] = (1/Test_Set_Size)*sum((y_test - y_predict)^2)
}

lasso_cv_MSE = mean(lasso_MSE)

# -----------------------------------------------------------------------------
# ----------------- 3.4 Plotting the MSE as lambda varies ---------------------
# -----------------------------------------------------------------------------

# Initialize the necessary vectors. For simplicity, we will not use cross-
# validation. Instead we keep using the same training and test sets.
Training_Set_Size = 10000

lambdas = 10^seq(5, -5, by = -.1)
lasso_betas = matrix(0L, 679, length(lambdas))
lasso_MSE = rep(0 , length(lambdas))
training = 1:Training_Set_Size
testset = (Training_Set_Size + 1):15000

# Store the selected training data into a data matrix X and target vector y
X = model.matrix(rating~., epi_r)[,-1][c(training),]
y = epi_r$rating[training]

# Store the selected test data into a data matrix X and target vector y
X_test = model.matrix(rating~., epi_r)[c(testset),-1]
y_test = epi_r$rating[testset]

# Loop over all lambdas, construct the model and store the MSE and coefficients.
for (i in 1:length(lambdas)) {
  lambda = lambdas[i]
  
  # Apply ridge regression on the data. Below we create a model based on 4
  # predictors for the variable 'Salary'.
  lasso_model <- glmnet(X, y, alpha = 1, lambda = lambda, 
                        intercept = TRUE, standardize = TRUE)
  
  # Store the regression coefficients in a vector
  lasso_betas[,i] = predict(lasso_model, type = "coefficients", s = lambda)[1:679]
  
  # Test the model with the test set
  y_predict = predict(lasso_model, X_test, s = lambda)
  
  # MSSE = Mean Sum of Squared Errors
  lasso_MSE[i] = (1/length(testset))*sum((y_test - y_predict)^2)
}

# Plot the MSE for different values of lambda
best_MSE_index = which(lasso_MSE == min(lasso_MSE))
plot(log(lambdas), lasso_MSE, "l",
     main = "A plot of the MSE (Lasso) in function of lambda")
lines(log(lambdas), rep(LS_MSE, length(lambdas)), "l", col ='red', lty = 3)
points(x = log(lambdas[best_MSE_index]), y = lasso_MSE[best_MSE_index],
       pch = 25, col = 'blue', cex = 1, bg = 'blue')

# Plot the coefficients of the ridge model with respect to lambda
cl <- rainbow(679)
plot(log(lambdas), lasso_betas[2,], "l", col = cl[2], ylim = c(-5,5), 
     ylab = "Coefficients", 
     main = "A plot of the coefficients (Lasso) in function of lambda")
lines(log(lambdas), rep(0, length(lambdas)), lty = 3)
for(i in 2:679) {
  lines(log(lambdas), lasso_betas[i,], "l", col = cl[i])
}

# -----------------------------------------------------------------------------
# -------------- 3.5 Plotting the MSE as lambda varies (both) -----------------
# -----------------------------------------------------------------------------

plot(log(lambdas), ridge_MSE, "l", col = 'blue', ylab = "MSE",
     main = "The MSE of the ridge and lasso model")
lines(log(lambdas), lasso_MSE, "l", col = 'red')
legend(-10, 1.73, legend = c("Ridge", "Lasso"), col = c('blue', 'red'),
       lty=1:2, cex=1)

# -----------------------------------------------------------------------------
# 4. Investigate which combination of ingredients will lead to the highest
# rating according to ridge regression. Investigate which proportion of nutrit-
# ional values will lead to the highest rating according to ridge regression.
# What would be the 'ideal' recipe?
# -----------------------------------------------------------------------------

epi_r_nutrients = epi_r[1:5]
ingredient_index = 1:679
ingredient_index = ingredient_index[!(ingredient_index %in% 2:5)]
epi_r_ingredients = epi_r[ingredient_index]

# Apply the lasso on the first data set
X = model.matrix(rating~., epi_r_nutrients)[,-1]
y = epi_r$rating
lambdas = 10^seq(5, -5, by = -.1)

lasso_nutrients = cv.glmnet(X, y, alpha = 1, lambda = lambdas, intercept = TRUE,
                            standardize = TRUE)
lambda_best = lasso_nutrients$lambda.min
lasso_nutrients = glmnet(X, y, alpha = 1, lambda = lambda_best, intercept = TRUE,
                         standardize = TRUE)
lambda_best
coef(lasso_nutrients)

# Apply the lasso on the second data set
X = model.matrix(rating~., epi_r_ingredients)[,-1]
y = epi_r$rating
lambdas = 10^seq(5, -5, by = -.5)

lasso_ingredients = cv.glmnet(X, y, alpha = 1, lambda = lambdas, intercept = TRUE,
                            standardize = TRUE)
lambda_best = lasso_ingredients$lambda.min
lasso_ingredients = glmnet(X, y, alpha = 1, lambda = lambda_best, intercept = TRUE,
                         standardize = TRUE)

# Find the indices of the 10 largest coefficients
coeff = predict(lasso_ingredients, type = "coefficients", s = lambda_best)
sorted_coeff = sort(coeff, decreasing = TRUE)

smallest = 675
highest = 1
highest_values = rep(0, 11)
for(i in 1:11) {
  if (abs(sorted_coeff[smallest]) > sorted_coeff[highest]) {
    highest_values[i] = sorted_coeff[smallest]
    smallest = smallest - 1
  } else {
    highest_values[i] = sorted_coeff[highest]
    highest = highest + 1
  }
}
highest_values = highest_values[!highest_values == 0]
most_important = which(coeff[1:675] %in% highest_values)[]

names(epi_r_ingredients[most_important])
