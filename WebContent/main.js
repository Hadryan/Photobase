angular.module('Photoshop', ['ngRoute'])
.run(function($rootScope){
	$rootScope.Singleton = {
		SessionInfo:{
			test: false
		},
		ImageInfoBackup:{
			byteArray: ""
		},
		ImageDataBackup:{
			byteArray: ""
		}
	}
})
.config(['$routeProvider', function($routeProvider) {
    $routeProvider
    	.when("/home", {
    		templateUrl: "home.html",
    		controller: 'HomeController'
    	})
	    .when("/login", {
	        templateUrl : "login.html",
	        controller: 'UserController'
	    })
	    .when("/register", {
	        templateUrl : "register.html",
	        controller: 'UserController'
	    })
	    .when("/forgot_password",{
	    	templateUrl: "forgot_password.html",
	    	controller: 'UserController'
	    })
	    .when("/reset_password/:token",{
	    	templateUrl: "reset_password.html",
	    	controller: 'ResetPasswordController'
	    })
	    .when("/logout",{
	    	templateUrl: "home.html",
	    	controller: 'LogoutController'
	    })
	    .when("/upload_image",{
	    	templateUrl: "upload_image.html",
	    	controller: 'ImageController'
	    })
	    .when("/image_view",{
	    	templateUrl: "image_view.html",
	    	controller: 'ImageViewController'
	    })
	    .when("/author_profile",{
	    	templateUrl: "author_profile.html",
	    	controller: 'AuthorController'
	    })
	    .when("/shopping_cart",{
	    	templateUrl: "shopping_cart.html",
	    	controller: 'ShoppingCartController'
	    })
	    .when("/checkout",{
	    	templateUrl: "checkout.html",
	    	controller: 'CheckoutController'
	    })
	    .when("/add_card",{
	    	templateUrl: "add_card.html",
	    	controller: 'AddCardController'
	    })
	    .when("/author_application",{
	    	templateUrl: "author_application.html",
	    	controller: 'AuthorApplicationController'
	    })
	    .when("/admin_homepage",{
	    	templateUrl: "admin_homepage.html",
	    	controller: 'AdminHomepageController'
	    })
	    .when("/add_categories",{
	    	templateUrl: "add_categories.html",
	    	controller: 'AddCategoriesController'
	    })
	    .when("/change_password",{
	    	templateUrl: "change_password.html",
	    	controller: 'ChangePasswordController'
	    })
	    .when("/operator_homepage",{
	    	templateUrl: "operator_homepage.html",
	    	controller: 'OperatorHomepageController'
	    })
	    .when("/pending_image_preview",{
	    	templateUrl: "pending_image_preview.html",
	    	controller: 'PendingImagePreviewController'
	    })
	    .when("/manage_users",{
	    	templateUrl: "manage_users.html",
	    	controller: 'ManageUsersController'
	    })
	    .when("/author_applications",{
	    	templateUrl: "author_applications.html",
	    	controller: 'AuthorApplicationsController'
	    })
	    .when("/application_view",{
	    	templateUrl: "application_view.html",
	    	controller: 'ApplicationViewController'
	    })
	    .when("/category_statistics",{
	    	templateUrl: "category_statistics.html",
	    	controller: 'CategoryStatisticsController'
	    })
	    .when("/user_statistics_view",{
	    	templateUrl: "user_statistics_view.html",
	    	controller: 'UserStatisticsViewController'
	    })
	    .when("/category_statistics_view",{
	    	templateUrl: "category_statistics_view.html",
	    	controller: 'CategoryStatisticsViewController'
	    })
	    .when("/delete_account",{
	    	templateUrl: "delete_account.html",
	    	controller: 'DeleteAccountController'
	    })
	    .when("/firm_registration",{
	    	templateUrl: "firm_registration.html",
	    	controller: 'FirmRegistrationController'
	    })
	    .when("/firm_applications",{
	    	templateUrl: "firm_applications.html",
	    	controller: 'FirmApplicationsController'
	    })
	    .when("/firm_operator_homepage",{
	    	templateUrl: "firm_operator_homepage.html",
	    	controller: 'FirmOperatorHomepageController'
	    })
	    .when("/apply_to_firm",{
	    	templateUrl: "apply_to_firm.html",
	    	controller: 'ApplyToFirmController'
	    })
	    .when("/firm_author_applications",{
	    	templateUrl: "firm_author_applications.html",
	    	controller: 'FirmAuthorApplicationsController'
	    })
	    .when("/leave_firm",{
	    	templateUrl: "leave_firm.html",
	    	controller: 'LeaveFirmController'
	    })
	    .when("/my_uploads",{
	    	templateUrl: "my_uploads.html",
	    	controller: 'MyUploadsController'
	    })
	    .when("/owned_images",{
	    	templateUrl: "owned_images.html",
	    	controller: 'OwnedImagesController'
	    })
	    .otherwise({
	    	redirectTo: '/home'
	    });
}])
.controller('HeaderController', ['$scope', '$rootScope', '$window', function($scope, $rootScope, $window){
	
	var init = function(){
		if ($window.sessionStorage.getItem("logged") === "true"){
			$rootScope.Singleton.SessionInfo.test = true;
			$rootScope.Singleton.SessionInfo.userRole = $window.sessionStorage.getItem("userRole");
			$rootScope.Singleton.SessionInfo.firmOperator = $window.sessionStorage.getItem("firmOperator");
			$rootScope.Singleton.SessionInfo.firmAuthor = $window.sessionStorage.getItem("firmAuthor");
			$rootScope.Singleton.SessionInfo.appliedToFirm = $window.sessionStorage.getItem("appliedToFirm");
		}
	}
	
	init();
	
}])
.controller('HomeController', ['$scope', '$rootScope', '$window', 'ImageService', '$location', function($scope, $rootScope, $window, ImageService, $location){
	
	$scope.imageInfo = [];
	$scope.imageData = [];
	
	$scope.tempInfoList = [];
	
	$scope.previous = true;
	$scope.next = false;
	$scope.pageCounter = 0;
	
	$scope.comboBox = 'Date/1';
	
	$scope.currentSearchInput = '';
	$scope.currentSearchCriteria = '';
	
	$scope.previewImage = function(id){
		$window.sessionStorage.setItem("id", id);
		$location.path('/image_view');
	}
	
	$scope.nextPage = function(){
		$scope.imageData = [];
		if ($scope.currentSearchInput != '')
			if ($scope.currentSearchCriteria != 'Keywords')
				getSearchData($scope.currentSearchCriteria, $scope.currentSearchCriteria + '/' + $scope.currentSearchInput + '/false/false');
			else {
				var temp = {};
				temp.entry = $scope.currentSearchInput;
				temp.init = false;
				temp.reverse = false;
				getSearchData('Keywords', temp);
			}
		else
			init($scope.comboBox + '/false/false');
		$scope.previous = false;
		console.log($scope.previous);
		$scope.pageCounter = $scope.pageCounter + 1;
	}
	
	$scope.previousPage = function(){
		$scope.imageData = [];
		if ($scope.currentSearchInput != ''){
				if ($scope.currentSearchCriteria !='Keywords')
					getSearchData($scope.currentSearchCriteria, $scope.currentSearchCriteria + '/' + $scope.currentSearchInput + '/false/true');
				else {
					var temp = {};
					temp.entry = $scope.currentSearchInput;
					temp.init = false;
					temp.reverse = true;
					getSearchData('Keywords', temp);
				}
		}
		else
			init($scope.comboBox + '/false/true');
		$scope.next = false;
		$scope.pageCounter = $scope.pageCounter - 1;
		if ($scope.pageCounter == 0){
			$scope.previous = true;
		}
	}
	
	$scope.onSelect = function(){
		$scope.currentSearchInput = '';
		$scope.currentSearchCriteria = '';
		$scope.searchInput = '';
		$scope.searchCriteria = '';
		$scope.imageData = [];
		$scope.previous = true;
		$scope.next = false;
		$scope.pageCounter = 0;
		init($scope.comboBox + '/true/false');
	}
	
	$scope.search = function(){
		$scope.previous = true;
		$scope.next = false;
		$scope.imageData = [];
		$scope.currentSearchInput = $scope.searchInput;
		$scope.currentSearchCriteria = $scope.searchCriteria;
		if ($scope.searchCriteria == 'Keywords'){
			var temp = {};
			temp.entry = $scope.currentSearchInput;
			temp.init = true;
			temp.reverse = false;
			getSearchData('Keywords', temp);
		} else {
			getSearchData('null', $scope.searchCriteria + '/' + $scope.searchInput + '/true/false');
		}
	}
	
	var getSearchData = function(cat, init){
		$scope.imageInfo = ImageService.getSearch(cat, init).then(function(response){
			$scope.imageInfo = response.data;
			if ($scope.imageInfo.length < 10)
				$scope.next = true;
		}).then(function(){
			$scope.imageInfo.forEach(function(info){
				var temp = {};
				temp.info = info;
				temp.data = ImageService.getImageThumbnail(info).then(function(response){
					temp.data = response.data;
				})
				$scope.imageData.push(temp);
			})
		})
	}
	
	var init = function(init){
		$scope.imageInfo = ImageService.getSorted(init).then(function(response){
			$scope.imageInfo = response.data;
			if ($scope.imageInfo.length < 10){
				$scope.next = true;
			}
			console.log($scope.imageInfo);
		}).then(function(){
			$scope.imageInfo.forEach(function(info){
				var temp = {};
				temp.info = info;
				temp.data = ImageService.getImageThumbnail(info).then(function(response){
					temp.data = response.data;
				})
				$scope.imageData.push(temp);
			})
		})
	}
	
	init("Date/1/true/false");
	
}])
.controller('ImageViewController', ['$scope', '$window', 'ImageService', 'UserService', '$location', function($scope, $window, ImageService, UserService, $location){
	
	$scope.id = {};
	$scope.imageInfo = {};
	$scope.imageData = {};
	
	$scope.message = '';
	
	$scope.regex = "/^-?[0-9][^\.]*$/";
	
	$scope.viewAuthor = function(){
		$window.sessionStorage.setItem("author_id", ''+$scope.imageInfo.authorsUsersId);
		$window.sessionStorage.setItem("author_username", ''+$scope.imageInfo.author.username);
		$location.path('/author_profile');
	}
	
	$scope.rateImage = function(){
		UserService.rateImage($scope.id, $scope.rating).then(function(response){
			if (response.data.startsWith('-')){
				$scope.message = response.data.substring(1);
			} else
				$scope.message = response.data;
		})
	}
	
	$scope.addToCart = function(item){
		UserService.addToCart(item).then(function(response){
			if (response.data == 'true'){
				$scope.message = 'Item added to cart';
			} else {
				$scope.message = 'Server error, please try again';
			}
		})
	}
	
	var init = function(){
			$scope.id = $window.sessionStorage.getItem("id");
			$scope.imageInfo = ImageService.getById($scope.id).then(function(response){
				$scope.imageInfo = response.data;
				$scope.imageData = ImageService.getImagePreview($scope.imageInfo).then(function(response){
					$scope.imageData = response.data;
					$scope.imageInfo.categories = ImageService.getCategoriesOfImage($scope.id).then(function(response){
						$scope.imageInfo.categories = response.data;
					})
					$scope.imageInfo.imageResData = ImageService.getAvailableResolutions($scope.id).then(function(response){
						$scope.imageInfo.imageResData = response.data;
					})
					$scope.imageInfo.resolutions = ImageService.getResolutionsOfImage($scope.id).then(function(response){
						$scope.imageInfo.resolutions = response.data;
					})
					$scope.imageInfo.author = UserService.getById($scope.imageInfo.authorsUsersId).then(function(response){
						$scope.imageInfo.author = response.data;
					})
				})
			})
	}
	
	init();
	
}])
.controller('UserController', ['$scope', 'UserService', '$rootScope', '$location', '$window', function($scope, UserService, $rootScope, $location, $window){
	
	$scope.message = ' ';
	
	$scope.login = function(){
		var user = {};
		user = angular.copy($scope.user);
		UserService.login(user).then(function(response){
			if (!response.data.startsWith("-")){
				$scope.message = 'Successfully logged in, welcome.';
				$rootScope.Singleton.SessionInfo.test = true;
				$rootScope.Singleton.SessionInfo.userRole = response.data;
				$window.sessionStorage.setItem("logged", "true");
				$window.sessionStorage.setItem("userRole", response.data);
				
				if (response.data == 'prodavac'){
					UserService.getAuthorFirmId().then(function(response){
						if (response.data != 0){
							$rootScope.Singleton.SessionInfo.firmAuthor = 'firmAuthor';
							$window.sessionStorage.setItem("firmAuthor", "firmAuthor");
							UserService.checkIfAppliedToFirm().then(function(response){
								if (response.data == 'true'){
									$rootScope.Singleton.SessionInfo.appliedToFirm = 'appliedToFirm';
									$window.sessionStorage.setItem("appliedToFirm", "appliedToFirm");
								}
							})
						}
					})
				}
				
				$location.path('/home');
			}
			else {
				if (response.data == '-operator'){
					UserService.operatorLogin(user).then(function(response){
						if (!response.data.startsWith('-')){
							$scope.message = 'Successfully logged in, welcome.';
							$rootScope.Singleton.SessionInfo.test = true;
							
							if (response.data == 'newFirmOperator'){
								$rootScope.Singleton.SessionInfo.operatorType = 'newOperator';
								$window.sessionStorage.setItem("operatorType", "newOperator");
								$rootScope.Singleton.SessionInfo.firmOperator = 'firmOperator';
								$window.sessionStorage.setItem("firmOperator", "firmOperator");
							}
							
							if (response.data == 'FirmOperator'){
								$rootScope.Singleton.SessionInfo.firmOperator = 'firmOperator';
								$window.sessionStorage.setItem("firmOperator", "firmOperator");
							}
							
							if (response.data == 'newOperator'){
								$rootScope.Singleton.SessionInfo.operatorType = 'newOperator';
								$window.sessionStorage.setItem("operatorType", "newOperator");
							}
							
							$rootScope.Singleton.SessionInfo.userRole = "operator";
							$window.sessionStorage.setItem("logged", "true");
							$window.sessionStorage.setItem("userRole", "operator");
							
							if (response.data == 'newOperator' || response.data == 'newFirmOperator')
								$location.path('/change_password');
							else if (response.data == 'FirmOperator')
								$location.path('/firm_operator_homepage');
							else
								$location.path('/operator_homepage');
						} else {
							$scope.message = response.data.substring(1, response.data.length);
						}
					})
				} else if (response.data == '-admin'){
					UserService.adminLogin(user).then(function(response){
						if (!response.data.startsWith("-")){
							$scope.message = 'Successfully logged in, welcome.';
							$rootScope.Singleton.SessionInfo.test = true;
							$rootScope.Singleton.SessionInfo.userRole = response.data;
							$window.sessionStorage.setItem("logged", "true");
							$window.sessionStorage.setItem("userRole", response.data);
							$location.path('/admin_homepage');
						} else {
							$scope.message = response.data.substring(1, response.data.length);
						}
					})
				} else {
					$scope.message = response.data.substring(1, response.data.length);
				}
			}
		});
	}
	
	$scope.register = function(){
		var user = {};
		user = angular.copy($scope.user);
		UserService.register(user).then(function(response){
			if (response.data == 'true')
				$scope.message = 'Successfully registered, account confirmation mail has been sent';
			else
				$scope.message = 'The username you have entered is already taken';
		});
	}
	
	$scope.forgotPassword = function(){
		var user = {};
		user = angular.copy($scope.user);
		UserService.forgotPassword(user).then(function(response){
			if (response.data == 'true')
				$scope.message = 'A password reset email has been sent';
			else
				$scope.message = 'Invalid username';
		});
	}
	
}])
.controller('AuthorController', ['UserService', '$scope', '$location', '$window', function(UserService, $scope, $location, $window){
	
	$scope.authorInfo = {};
	$scope.firmInfo = {};
	$scope.commentInfo = [];
	
	$scope.regex = "/^-?[0-9][^\.]*$/";
	
	$scope.rateAuthor = function(){
		UserService.rateAuthor($scope.authorInfo.id, $scope.rating).then(function(response){
			console.log(response.data);
			if (response.data.startsWith('-')){
				$scope.message = response.data.substring(1);
			} else {
				$scope.message = response.data;
			}
		})
	}
	
	$scope.commentAuthor = function(){
		UserService.comment($scope.authorInfo.id, $scope.comment).then(function(response){
			if (response.data.startsWith('-')){
				$scope.message = response.data.substring(1);
			} else 
				$window.location.reload();
		})
	}
	
	var init = function(){
		$scope.authorInfo.id = $window.sessionStorage.getItem("author_id");
		$scope.authorInfo.username = $window.sessionStorage.getItem("author_username");
		$scope.authorInfo.author = UserService.getAuthorById($scope.authorInfo.id).then(function(response){
			$scope.authorInfo.author = response.data;
			console.log($scope.authorInfo.author);
			$scope.firmInfo.name = 'independent author';
			if ($scope.authorInfo.author.firm_id != 0)
				$scope.authorInfo.author.firm = UserService.getFirmById($scope.authorInfo.author.firm_id).then(function(response){
					$scope.authorInfo.author.firm = response.data;
					$scope.firmInfo.name = $scope.authorInfo.author.firm.name;
				})
		})
		$scope.authorInfo.comments = UserService.getComments($scope.authorInfo.id).then(function(response){
			$scope.authorInfo.comments = response.data;
			$scope.authorInfo.comments.forEach(function(comment){
				var temp = {};
				temp.user = UserService.getById(comment.users_id).then(function(response){
					temp.user = response.data.username;
				})
				temp.text = comment.text;
				$scope.commentInfo.push(temp);
			})
		})
		
	}
	
	init();
	
}])
.controller('ShoppingCartController', ['UserService', 'ImageService', '$rootScope', '$scope', '$location', '$window', function(UserService, ImageService,
		$rootScope, $scope, $location, $window){
	
	$scope.message = '';
	$scope.shoppingCart = {};
	
	$scope.empty = true;
	
	$scope.checkout = function(){
		$window.sessionStorage.setItem('cartTotal', '' + $scope.shoppingCart.price);
		$location.path('/checkout');
	}
	
	$scope.removeItem = function(item){
		var temp = {};
		temp.images_id = item.images_id;
		temp.image_resolutions_id = item.image_resolutions_id;
		temp.price = item.price;
		UserService.removeFromCart(temp).then(function(response){
			if (response.data == 'true'){
				init();
			} else {
				message = 'Internal server error';
			}
		})
	}
	
	var init = function(){
		$scope.shoppingCart = UserService.getShoppingCart().then(function(response){
			$scope.shoppingCart = response.data;
			if ($scope.shoppingCart == '' || $scope.shoppingCart.items.length == 0){
				$scope.message = 'Shopping cart empty';
				$scope.empty = true;
				return;
			}
			$scope.empty = false;
			$scope.shoppingCart.price = Number($scope.shoppingCart.price).toFixed(2);
			$scope.shoppingCart.items.forEach(function(item){
				item.resolutionData = ImageService.getResolutionById(item.image_resolutions_id).then(function(response){
					item.resolutionData = response.data;
				})
				item.imageData = ImageService.getById(item.images_id).then(function(response){
					item.name = response.data.name;
				})
			})
			console.log($scope.shoppingCart);
		})
	}
	init();
	
}])
.controller('CheckoutController', ['UserService', '$rootScope', '$location', '$scope', '$window', function(UserService, $rootScope, $location, $scope, $window){
	
	$scope.cards = [];
	$scope.total = '';
	$scope.message = '';
	
	$scope.cardNumberRegex = /[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}/;
	$scope.validationCodeRegex = /[0-9]{3}/;
	$scope.expirationDateRegex = /^\d{4}\-(0?[1-9]|1[012])\-(0?[1-9]|[12][0-9]|3[01])$/
	
	$scope.addCard = function(){
		$location.path('/add_card');
	}
	
	$scope.buy = function(){
		var temp = {};
		temp.id = $scope.selectedCard;
		UserService.buy(temp).then(function(response){
			if (response.data.startsWith('-')){
				$scope.message = response.data.substring(1);
			} else {
				$window.sessionStorage.removeItem("cartTotal");
				$scope.message = response.data;
			}
		})
	}
	
	var init = function(){
		$scope.total = $window.sessionStorage.getItem("cartTotal");
		$scope.cards = UserService.getCreditcards().then(function(response){
			$scope.cards = response.data;
		})
	}
	
	init();
	
}])
.controller('AddCardController', ['UserService', '$rootScope', '$location', '$window', '$scope', function(UserService, $rootScope, $location, $window, $scope){
	
	$scope.cardNumberRegex = /[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}/;
	$scope.validationCodeRegex = /[0-9][0-9][0-9]/;
	$scope.expirationDateRegex = /^\d{4}\-(0?[1-9]|1[012])\-(0?[1-9]|[12][0-9]|3[01])$/
	
	$scope.message = '';
	
	$scope.cards = [];
	
	$scope.setAuthorCard = function(){
		UserService.setAuthorCard($scope.selectedCard).then(function(response){
			if (response.data == 'true'){
				$scope.message = 'Default card set';
			} else {
				$scope.message = 'An error occurred';
			}
		})
	}
		
	$scope.addCard = function(){
		var cd = {};
		cd = angular.copy($scope.card);
		console.log(cd);
		UserService.addCard(cd).then(function(response){
			if (response.data.startsWith('-')){
				$scope.message = response.data.substring(1);
			} else {
				$scope.cards.push(cd);
				document.getElementById("authorCardForm").reset();
				$scope.message = response.data;
			}
		})
	}
	
	var init = function(){
		
		$scope.cards = UserService.getCreditcards().then(function(response){
			$scope.cards = response.data;
		})
		
	}
	
	init();
	
}])
.controller('AuthorApplicationController', ['UserService', '$rootScope', '$location', '$window', '$scope', function(UserService, $rootScope, $location, $window, $scope){
	
	$scope.message = '';
	
	$scope.apply = function(){
		var multipartFile = {};
		multipartFile.data1 = $scope.file1;
		multipartFile.data2 = $scope.file2;
		multipartFile.data3 = $scope.file3;
		multipartFile.data4 = $scope.file4;
		multipartFile.data5 = $scope.file5;
		multipartFile.data6 = $scope.file6;
		multipartFile.data7 = $scope.file7;
		multipartFile.data8 = $scope.file8;
		multipartFile.data9 = $scope.file9;
		multipartFile.data10 = $scope.file10;
		UserService.applyForAuthor(multipartFile).then(function(response){
			if (!response.data.startsWith("-")){
				$scope.message = response.data;
			} else {
				$scope.message = response.data.substring(1, response.data.length);
			}
		})
	}
	
}])
.controller('AdminHomepageController', ['UserService', '$rootScope', '$location', '$window', '$scope', function(UserService, $rootScope, $location, $window, $scope){
	
	$scope.operators = [];
	
	$scope.message = '';
	
	$scope.addOperator = function(operator){
		console.log(operator);
		UserService.addOperator(operator).then(function(response){
			if (response.data.startsWith("-")){
				$scope.message = response.data.substring(1);
			} else{
				document.getElementById("adminHomepage").reset();
				init();
				$scope.message = response.data;
			}
		})
	}
	
	$scope.removeOperator = function(operator){
		if (confirm("Are you sure you want to delete the following operator:\n"+operator.username + " - " + operator.firmId)){
			UserService.removeOperator(operator.id).then(function(response){
				if (response.data == 'true'){
					init();
					$scope.message = 'Successfully removed operator';
				} else {
					$scope.message = 'An eeror occured';
				}
			})
		}
	}
	
	var init = function(){
		$scope.operators = UserService.getOperators().then(function(response){
			$scope.operators = response.data;
			$scope.operators.forEach(function(operator){
				console.log(operator);
				if (operator.firmId == 0){
					operator.firmId = 'independent';
				} else {
					UserService.getFirmById(operator.firmId).then(function(response){
						operator.firmId = response.data.name;
					})
				}
			})
		})
	}
	
	init();
	
}])
.controller('AddCategoriesController', ['UserService', '$rootScope', '$location', 'ImageService', '$scope', function(UserService, $rootScope, $location, ImageService, $scope){
	
	$scope.message = '';
	$scope.categories = [];
	
	$scope.addCategory = function(category){
		console.log('usao');
		UserService.addCategory(category).then(function(response){
			if (response.data.startsWith("-")){
				$scope.message = response.data.substring(1);
			} else {
				document.getElementById("addCategoryForm").reset();
				$scope.message = response.data;
				init();
			}
		})
	}
	
	var init = function(){
		$scope.categories  = ImageService.getCategories().then(function(response){
			$scope.categories = response.data;
		})
	}
	
	init();
	
}])
.controller('ChangePasswordController', ['UserService', '$rootScope', '$location', '$window', '$scope', function(UserService, $rootScope, $location, $window, $scope){
	
	$scope.message = '';
	
	$scope.userChangePassword = function(currPass, newPass){
		UserService.changeUserPass(currPass, newPass).then(function(response){
			if (response.data.startsWith("-")){
				$scope.message = response.data.substring(1);
			} else {
				$scope.message = response.data;
			}
		})
	}
	
	$scope.changePassword = function(pass){
		var temp = {};
		temp.password = pass;
		UserService.changeOperatorPassword(temp).then(function(response){
			if (response.data == 'true'){
				$rootScope.Singleton.SessionInfo.operatorType = '';
				$window.sessionStorage.removeItem("operatorType");
				$scope.message = 'Account activated';
				if ($rootScope.Singleton.SessionInfo.firmOperator == 'firmOperator')
					$location.path('/firm_operator_homepage');
				else
					$location.path('/operator_homepage')
			} else {
				$scope.message = 'Account already fully activated';
			}
		})
	}
	
	var init = function(){
		$rootScope.Singleton.SessionInfo.operatorType = $window.sessionStorage.getItem("operatorType");
		$rootScope.Singleton.SessionInfo.userRole = $window.sessionStorage.getItem("userRole");
		$rootScope.Singleton.SessionInfo.firmOperator = $window.sessionStorage.getItem("firmOperator");
	}
	
	init();
	
}])
.controller('OperatorHomepageController', ['UserService', '$rootScope', '$location', '$window', '$scope', 'ImageService',
	function(UserService, $rootScope, $location, $window, $scope, ImageService){
	
	$scope.imageInfo = [];
	$scope.imageData = [];
	
	$scope.previous = true;
	$scope.next = false;
	$scope.pageCounter = 0;
	
	$scope.previewImage = function(id){
		$window.sessionStorage.setItem("id", id);
		$location.path('/pending_image_preview');
	}
	
	$scope.nextPage = function(){
		$scope.imageData = [];
		init("false/false");
		$scope.previous = false;
		$scope.pageCounter = $scope.pageCounter + 1;
	}
	
	$scope.previousPage = function(){
		$scope.imageData = [];
		init("true/true");
		$scope.next = false;
		$scope.pageCounter = $scope.pageCounter - 1;
		if ($scope.pageCounter == 0){
			$scope.previous = true;
		}
	}
	
	var init = function(crit){
		$scope.imageInfo = UserService.getPendingImages(crit).then(function(response){
			$scope.imageInfo = response.data;
			if ($scope.imageInfo.length < 10){
				$scope.next = true;
			}
			console.log($scope.imageInfo);
		}).then(function(){
			$scope.imageInfo.forEach(function(info){
				var temp = {};
				temp.info = info;
				temp.data = ImageService.getImageThumbnail(info).then(function(response){
					temp.data = response.data;
				})
				$scope.imageData.push(temp);
			})
		})
	}
	
	init("true/false");
	
}])
.controller('PendingImagePreviewController', ['UserService', '$rootScope', '$location', '$window', '$scope', 'ImageService',
	function(UserService, $rootScope, $location, $window, $scope, ImageService){
	
	$scope.id = {};
	$scope.imageInfo = {};
	$scope.imageData = {};
	
	$scope.message = '';
	
	$scope.approve = function(id){
		var temp = {};
		temp.id = id;
		UserService.approveImage(temp).then(function(response){
			if (response.data == 'true'){
				$scope.message = 'Image approved';
			} else {
				$scope.message = 'Error, check if you have activated your account.';
			}
		})
	}
	
	var init = function(){
		$scope.id = $window.sessionStorage.getItem("id");
		$scope.imageInfo = ImageService.getById($scope.id).then(function(response){
			$scope.imageInfo = response.data;
			$scope.imageData = ImageService.getImagePreview($scope.imageInfo).then(function(response){
				$scope.imageData = response.data;
				$scope.imageInfo.categories = ImageService.getCategoriesOfImage($scope.id).then(function(response){
					$scope.imageInfo.categories = response.data;
				})
				$scope.imageInfo.imageResData = ImageService.getAvailableResolutions($scope.id).then(function(response){
					$scope.imageInfo.imageResData = response.data;
				})
				$scope.imageInfo.resolutions = ImageService.getResolutionsOfImage($scope.id).then(function(response){
					$scope.imageInfo.resolutions = response.data;
				})
				$scope.imageInfo.author = UserService.getById($scope.imageInfo.authorsUsersId).then(function(response){
					$scope.imageInfo.author = response.data;
				})
			})
		})
	}
	
	init();
	
}])
.controller('ManageUsersController', ['UserService', '$rootScope', '$location', '$window', '$scope',
	function(UserService, $rootScope, $location, $window, $scope){
	
	$scope.users = [];
	$scope.message = '';
	
	$scope.viewUserStatistics = function(user){
		$window.sessionStorage.setItem("userStatistics", ""+user.id);
		$location.path("/user_statistics_view");
	}
	
	$scope.ban = function(user){
		if (confirm("Are you sure you want to ban the following user:\n"+user.id + " - " + user.username + " - " + user.role + " " + user.extra)){
			var temp = {};
			temp.id = user.id;
			UserService.banUser(temp).then(function(response){
				if (response.data.startsWith("-")){
					$scope.message = response.data.substring(1);
				} else {
					$scope.message = response.data;
					init();
				}
			})
		}
	}
	
	var init = function(){
		$scope.users = UserService.getAllUsers().then(function(response){
			$scope.users = response.data;
			$scope.users.forEach(function(user){
				if (user.banned == 1)
					user.extra = '- BANNED';
				else if (user.deleted == 1)
					user.extra = '- DELETED';
				else
					user.extra = '';
			})
		})
	}
	
	init();
	
}])
.controller('AuthorApplicationsController', ['UserService', '$rootScope', '$location', '$window', '$scope',
	function(UserService, $rootScope, $location, $window, $scope){
	
	$scope.users = [];
	
	$scope.message = '';
	
	$scope.viewApplication = function(user){
		$window.sessionStorage.setItem("applicant_id", "" + user.id);
		$location.path('/application_view');
	}
	
	var init = function(){
		$scope.users = UserService.getApplicants().then(function(response){
			$scope.users = response.data;
		})
	}
	
	init();
	
}])
.controller('ApplicationViewController', ['UserService', '$rootScope', '$location', '$window', '$scope',
	function(UserService, $rootScope, $location, $window, $scope){
	
	$scope.userId = 0;
	$scope.message = '';
	
	$scope.images = [];
	
	$scope.regex = "/^-?[0-9][^\.]*$/";
	
	$scope.rateFirmApplicant = function(){
		var temp = {};
		temp.users_id = $scope.userId;
		UserService.approveFirmAuthor(temp).then(function(response){
			if (response.data.startsWith('-')){
				$scope.message = response.data.substring(1);
			} else {
				$scope.message = response.data;
			}
		})
	}
	
	$scope.rateApplicant = function(){
		UserService.rateApplicant($scope.userId, $scope.rating).then(function(response){
			if (response.data.startsWith("-")){
				$scope.message = response.data.substring(1);
			} else {
				$scope.message = response.data;
			}
		})
	}
	
	var init = function(){
		$rootScope.Singleton.SessionInfo.firmOperator = $window.sessionStorage.getItem("firmOperator");
		$scope.userId = $window.sessionStorage.getItem("applicant_id");
		var i;
		for (i = 0; i < 10; i++){
			UserService.getApplicantImage($scope.userId, i).then(function(response){
				$scope.images.push(response.data);
			})
		}
	}
	
	init();
	
}])
.controller('CategoryStatisticsViewController', ['UserService', '$rootScope', '$location', '$window', '$scope',
	function(UserService, $rootScope, $location, $window, $scope){
	
	$scope.categoryId = 0;
	$scope.categoryName = '';
	
	$scope.temp = {};
	
	$scope.soldImagesCountByCategory = 0;
	$scope.resolutions = [];
	$scope.averagePriceInCategory = 0;
	
	var init = function(){
		$scope.temp.id = $window.sessionStorage.getItem("categoryId");
		$scope.temp.name = $window.sessionStorage.getItem("categoryName");

		console.log($scope.temp);
		
		$scope.soldImagesCountByCategory = UserService.soldImagesCountByCategory($scope.temp).then(function(response){
			$scope.soldImagesCountByCategory = response.data;
		})
		$scope.resolutions = UserService.mostFrequentlyBoughtImageResolutionsInCategory($scope.temp).then(function(response){
			$scope.resolutions = response.data;
		})
		$scope.averagePriceInCategory = UserService.averagePriceInCategory($scope.temp).then(function(response){
			$scope.averagePriceInCategory = Number(response.data).toFixed(2);
		})
	
	}
	
	init();
	
}])
.controller('CategoryStatisticsController', ['UserService', '$rootScope', '$location', '$window', '$scope', 'ImageService',
	function(UserService, $rootScope, $location, $window, $scope, ImageService){
	
	$scope.categories = [];
	
	$scope.viewCategoryStatistics = function(category){
		$window.sessionStorage.setItem("categoryId", ""+category.id);
		$window.sessionStorage.setItem("categoryName", category.name);
		$location.path('/category_statistics_view');
	}
	
	var init = function(){
		$scope.categories = ImageService.getCategories().then(function(response){
			$scope.categories = response.data;
		})
	}
	
	init();
	
}])
.controller('UserStatisticsViewController', ['UserService', '$rootScope', '$location', '$window', '$scope',
	function(UserService, $rootScope, $location, $window, $scope){
	
	$scope.user = {};
	$scope.soldImagesCount = 0;
	$scope.averagePriceByAuthor = 0;
	
	$scope.resolutions = [];
	
	var init = function(){
		var user_id = $window.sessionStorage.getItem("userStatistics");
		$scope.user = UserService.getById(user_id).then(function(response){
			$scope.user = response.data;
		}).then(function(){
			if ($scope.user.role == 'kupac'){
				$scope.resolutions = UserService.frequentlyBoughtResByUser($scope.user).then(function(response){
					$scope.resolutions = response.data;
				})
			} else {
				$scope.soldImagesCount = UserService.soldImagesCountByUser($scope.user).then(function(response){
					$scope.soldImagesCount = response.data;
				})
				$scope.averagePriceByAuthor = UserService.averagePriceByAuthor($scope.user).then(function(response){
					$scope.averagePriceByAuthor = Number(response.data).toFixed(2);
				})
				$scope.resolutions = UserService.frequentlyBoughtResByUser($scope.user).then(function(response){
					$scope.resolutions = response.data;
				})
			}
		})
	}
	
	init();
	
}])
.controller('DeleteAccountController', ['UserService', '$rootScope', '$location', '$window', '$scope', 
	function(UserService, $rootScope, $location, $window, $scope){
	
	$scope.message = '';
	
	$scope.deleteAccount = function(){
		UserService.deleteAccount().then(function(response){
			if (response.data == 'true'){
				$scope.message = 'Account deleted';
			} else {
				$scope.message = 'Your account is already banned';
			}
		})
	}
	
}])
.controller('FirmRegistrationController', ['UserService', '$rootScope', '$location', '$window', '$scope',
	function(UserService, $rootScope, $location, $window, $scope){
	
	$scope.pattern = /0.[0-9]{4}/
	$scope.message = '';
	
	$scope.register = function(){
		console.log($scope.firm);
		UserService.registerFirm($scope.firm).then(function(response){
			if (response.data == 'true'){
				$scope.message = 'Successfully registered';
			} else {
				$scope.message = 'Name or PIB already taken';
			}
		})
	}
	
		
}])
.controller('FirmApplicationsController', ['UserService', '$rootScope', '$location', '$window', '$scope',
	function(UserService, $rootScope, $location, $window, $scope){
	
	$scope.message = '';
	$scope.firms = [];
	
	$scope.approve = function(firm){
		UserService.approveFirm(firm).then(function(response){
			if (response.data.startsWith('-')){
				$scope.message = response.data.substring(1);
			} else {
				$scope.message = response.data;
				init();
			}
		})
	}
	
	var init = function(){
		$scope.firms = UserService.getAppliedFirms().then(function(response){
			$scope.firms = response.data;
		})
	}
	
	init();
	
}])
.controller('FirmOperatorHomepageController', ['UserService', '$rootScope', '$location', '$window', '$scope',
	function(UserService, $rootScope, $location, $window, $scope){
	
	$scope.operators = [];
	$scope.message = '';
	
	$scope.addOperator = function(operator){
		
		UserService.addFirmOperator(operator).then(function(response){
			if (!response.data.startsWith('-')){
				$scope.message = response.data;
				init();
			} else {
				$scope.message = response.data.substring(1);
			}
		})
	}
	
	var init = function(){
		$scope.operators = UserService.getFirmOperators().then(function(response){
			$scope.operators = response.data;
		})
	}
	
	init();
	
}])
.controller('ApplyToFirmController', ['UserService', '$rootScope', '$location', '$window', '$scope',
	function(UserService, $rootScope, $location, $window, $scope){
	
	$scope.message = '';
	$scope.firms = '';
	
	$scope.apply = function(firm){
		UserService.applyToFirm(firm).then(function(response){
			if (response.data.startsWith('-')){
				$scope.message = response.data.substring(1);
			} else {
				$rootScope.Singleton.SessionInfo.appliedToFirm = 'appliedToFirm';
				$window.sessionStorage.setItem("appliedToFirm", "appliedToFirm");
				$scope.message = response.data;
			}
		})
	}
	
	var init = function(){
		$scope.firms = UserService.getAllFirms().then(function(response){
			$scope.firms = response.data;
		})
	}
	
	init();
	
}])
.controller('FirmAuthorApplicationsController', ['UserService', '$rootScope', '$location', '$window', '$scope',
	function(UserService, $rootScope, $location, $window, $scope){
	
	$scope.authors = [];
	$scope.users = [];
	
	$scope.viewApplication = function(user){
		$window.sessionStorage.setItem("applicant_id", "" + user.id);
		$location.path('/application_view');
	}
	
	var init = function(){
		$scope.authors = UserService.getAppliedAuthors().then(function(response){
			$scope.authors = response.data;
			$scope.authors.forEach(function(author){
				UserService.getById(author.users_id).then(function(response){
					$scope.users.push(response.data);
				})
			});
			
		})
	}
	
	init();
	
}])
.controller('LeaveFirmController', ['UserService', '$rootScope', '$location', '$window', '$scope',
	function(UserService, $rootScope, $location, $window, $scope){
	
	$scope.message = '';
	
	$scope.leaveFirm = function(){
		UserService.leaveFirm().then(function(response){
			if (response.data.startsWith('-')){
				$scope.message = response.data.substring(1);
			} else {
				$scope.message = response.data;
				$rootScope.Singleton.SessionInfo.firmAuthor = '';
				$window.sessionStorage.removeItem("firmAuthor");
			}
		})
	}
	
}])
.controller('MyUploadsController', ['UserService', '$rootScope', '$location', '$window', '$scope', 'ImageService',
	function(UserService, $rootScope, $location, $window, $scope, ImageService){
	
	$scope.images = [];
	$scope.imageData = [];
	
	$scope.message = '';
	
	$scope.deleteImage = function(pic){
		if (confirm("Are you sure you want to delete the following image: " + pic.info.name + " - " + pic.info.location)){
			UserService.deleteImage(pic.info).then(function(response){
				if (response.data == 'false'){
					$scope.message = "An error has occured";
				} else {
					$scope.message = "Image deleted";
				}
			})
		}
	}
	
	var init = function(){
		$scope.images = UserService.getImagesFromUser().then(function(response){
			$scope.images = response.data;
			$scope.images.forEach(function(img){
				var temp = {};
				temp.info = img;
				temp.data = ImageService.getImageThumbnail(img).then(function(response){
					temp.data = response.data;
				})
				$scope.imageData.push(temp);
			})
		})
	}
	
	init();
	
}])
.controller('OwnedImagesController', ['UserService', '$rootScope', '$location', '$window', '$scope',  'ImageService',
	function(UserService, $rootScope, $location, $window, $scope, ImageService){
	
	$scope.message = '';
	
	$scope.images = [];
	$scope.imageData = [];
	
	$scope.send = function(pic){
		UserService.sendImage(pic.info).then(function(response){
			if (response.data == 'true'){
				$scope.message = 'A mail containing the image has been sent';
			} else {
				$scope.message = 'An error has occurred';
			}
		})
	}
	
	var init = function(){
		$scope.images = UserService.getOwnedImages().then(function(response){
			$scope.images = response.data;
			$scope.images.forEach(function(img){
				var temp = {};
				temp.info = img;
				temp.data = ImageService.getImageThumbnail(img).then(function(response){
					temp.data = response.data;
				})
				$scope.imageData.push(temp);
			})
		})
	}
	
	init();
	
}])
.controller('LogoutController', ['UserService', '$rootScope', '$location', '$window', function(UserService, $rootScope, $location, $window){
	
	var init = function(){
		$rootScope.Singleton.SessionInfo.test = false;
		$rootScope.Singleton.SessionInfo.userRole = "";
		$rootScope.Singleton.SessionInfo.firmOperator = '';
		$rootScope.Singleton.SessionInfo.operatorType = '';
		$rootScope.Singleton.SessionInfo.firmAuthor = '';
		$rootScope.Singleton.SessionInfo.appliedToFirm = '';
		$window.sessionStorage.clear();
		UserService.logout();
		$location.path('/login')
	}
	init();
	
}])
.controller('ResetPasswordController', ['$scope', 'UserService', '$routeParams', function($scope, UserService, $routeParams){
	
	$scope.token = $routeParams.token;
	$scope.message = ' ';
	
	$scope.resetPassword = function(){
		$scope.message = 'usao';
		var user = {};
		user = angular.copy($scope.user);
		user.token = $scope.token;
		UserService.resetPassword(user).then(function(response){
			if (response.data == 'true')
				$scope.message = 'New password set.';
			else
				$scope.message = 'An error occured, please try again.';
		});
	}
	
}])
.controller('ImageController', ['$scope', 'ImageService', '$rootScope', '$location', function($scope, ImageService, $rootScope, $location){
	
	$scope.message = ' ';
	
	$scope.categories = [];
	$scope.resolutions = [];
	
	$scope.addedCategories = [];
	$scope.addedResolutions = [];
	
	$scope.finalCategories = [];
	$scope.finalResolutions = [];
	
	$scope.uploadImage = function(){
		if ($rootScope.Singleton.SessionInfo.userRole != "prodavac"){
			window.alert("Only an author has permission to upload images. To become an author, please apply.");
			$location.path('/home');
		} else {
			var image = {};
			image = angular.copy($scope.image);
			console.log(image.name);
			console.log(image.file);
		}
	}
	
	$scope.manageCategory = function(category, checked){
		if (checked == true){
			var index = $scope.addedCategories.indexOf(category);
			$scope.addedCategories.splice(index, 1);
		} else {
			$scope.addedCategories.push(category);
		}
		console.log($scope.addedCategories);
	}
	
	$scope.manageResolution = function(resolution, checked){
		if (checked == true){
			var index = $scope.addedResolutions.indexOf(resolution);
			$scope.addedResolutions.splice(index, 1);
		} else {
			$scope.addedResolutions.push(resolution);
		}
		console.log($scope.addedResolutions);
	}
	
	
	
	$scope.uploadImage = function(form){
		$scope.addedResolutions.forEach(function(resolution){
			var images_has_image_resolutions = {};
			images_has_image_resolutions.image_resolutions_id = resolution;
			images_has_image_resolutions.price = document.getElementById(resolution).value;
			$scope.finalResolutions.push(images_has_image_resolutions);
		});
		
		$scope.addedCategories.forEach(function(category){
			var cat = {};
			cat.id = category;
			$scope.finalCategories.push(cat);
		});
		var multipartFile = {};
		multipartFile.data = $scope.file;
		multipartFile.image = JSON.stringify($scope.image);
		var obj1 = {};
		var obj2 = {};
		obj1.items = $scope.finalResolutions;
		obj2.items = $scope.finalCategories;
		multipartFile.resolutions = JSON.stringify(obj1);
		multipartFile.categories = JSON.stringify(obj2);
		ImageService.upload(multipartFile).then(function(response){
			if (!response.data.startsWith("-")){
				$scope.message = response.data;
			} else {
				$scope.message = response.data.substring(1, response.data.length);
			}
			$scope.finalCategories = [];
			$scope.finalResolutions = [];
		});
	}
	
	
	
	//inicijalizacija i validacija forme
	$scope.checkBoxSelected = function(){
		if ($scope.addedResolutions.length<1)
			return true;
		else 
			return false;
	}
	
	var init = function(){
		$scope.categories = ImageService.getCategories().then(function(response){
			$scope.categories = response.data;
		});
		$scope.resolutions = ImageService.getResolutions().then(function(response){
			$scope.resolutions = response.data;
		})
	}
	
	init();
	
}])
.factory('UserService', ['$http', function($http){
	var service = {};
	
	service.login = function(user){
		return $http.post('http://localhost:8080/Photoshop/rest/user/login', user);
	}
	service.getById = function(id){
		return $http.get('http://localhost:8080/Photoshop/rest/user/getById/' + id);
	}
	service.getAuthorById = function(id){
		return $http.get('http://localhost:8080/Photoshop/rest/user/getAuthorById/' + id);
	}
	service.rateAuthor = function(authorId, rating){
		return $http.post('http://localhost:8080/Photoshop/rest/user/rateAuthor', "author_id=" + authorId
				+ "&value=" + rating, {headers: {
                	'Content-Type': 'application/x-www-form-urlencoded'
                }});
	}
	service.comment = function(authorId, text){
		return $http.post('http://localhost:8080/Photoshop/rest/user/comment', "author_id=" + authorId
				+ "&text=" + text, {headers: {
                	'Content-Type': 'application/x-www-form-urlencoded'
                }});
	}
	service.getComments = function(id){
		return $http.get('http://localhost:8080/Photoshop/rest/user/getComments/' + id);
	}
	service.getFirmById = function(id){
		return $http.get('http://localhost:8080/Photoshop/rest/firm/getById/' + id);
	}
	service.register = function(user){
		return $http.post('http://localhost:8080/Photoshop/rest/user/register', user);
	}
	service.forgotPassword = function(user){
		return $http.get('http://localhost:8080/Photoshop/rest/user/forgotPassword/' + user.username);
	}
	service.resetPassword = function(user){
		return $http.post('http://localhost:8080/Photoshop/rest/user/resetPassword', "token=" + user.token +
                "&password=" + user.password, {headers: {
                	'Content-Type': 'application/x-www-form-urlencoded'
                }}
		);
	}
	service.logout = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/user/logout');
	}
	service.addToCart = function(item){
		return $http.post('http://localhost:8080/Photoshop/rest/user/addToShoppingCart', item);
	}
	service.removeFromCart = function(item){
		return $http.post('http://localhost:8080/Photoshop/rest/user/removeFromShoppingCart', item);
	}
	service.getShoppingCart = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/user/getShoppingCart');
	}
	service.getCreditcards = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/user/getCreditcards');
	}
	service.addCard = function(card){
		return $http.post('http://localhost:8080/Photoshop/rest/user/addCreditcard', card);
	}
	service.buy = function(card){
		return $http.post('http://localhost:8080/Photoshop/rest/user/buy', card);
	}
	service.rateImage = function(imageId, rating){
		return $http.post('http://localhost:8080/Photoshop/rest/user/rateImage', "image_id=" + imageId
				+ "&value=" + rating, {headers: {
                	'Content-Type': 'application/x-www-form-urlencoded'
                }});
	}
	service.applyForAuthor = function(data){
		var fd = new FormData();
		for (var key in data)
			fd.append(key, data[key]);

		return $http.post('http://localhost:8080/Photoshop/rest/user/applyForAuthor', fd, {
			transformRequest: angular.identity,
			headers: {'Content-Type': undefined}
		});
	}
	service.adminLogin = function(user){
		return $http.post('http://localhost:8080/Photoshop/rest/admin/adminLogin', user);
	}
	service.operatorLogin = function(user){
		return $http.post('http://localhost:8080/Photoshop/rest/operator/login', user);
	}
	service.getOperators = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/admin/getOperators');
	}
	service.removeOperator = function(id){
		return $http.get('http://localhost:8080/Photoshop/rest/admin/removeOperator/' + id);
	}
	service.addOperator = function(operator){
		return $http.post('http://localhost:8080/Photoshop/rest/admin/addOperator', operator);
	}
	service.addCategory = function(category){
		return $http.get('http://localhost:8080/Photoshop/rest/admin/addCategory/' + category.name);
	}
	service.changeOperatorPassword = function(pass){
		return $http.post('http://localhost:8080/Photoshop/rest/operator/changePassword', pass);
	}
	service.getPendingImages = function(temp){
		return $http.get('http://localhost:8080/Photoshop/rest/operator/getPendingImages/' + temp);
	}
	service.approveImage = function(image){
		return $http.post('http://localhost:8080/Photoshop/rest/operator/approveImage', image);
	}
	service.getAllUsers = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/operator/getAllUsers');
	}
	service.banUser = function(user){
		return $http.post('http://localhost:8080/Photoshop/rest/operator/banUser', user);
	}
	service.getApplicants = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/operator/getApplicants');
	}
	service.getApplicantImage = function(id, index){
		return $http.get('http://localhost:8080/Photoshop/rest/operator/getApplicantImages/' + id + '/' + index);
	}
	service.rateApplicant = function(user_id, grade){
		return $http.post('http://localhost:8080/Photoshop/rest/operator/rateApplicant', "user_id=" + user_id
				+ "&grade=" + grade, {headers: {
                	'Content-Type': 'application/x-www-form-urlencoded'
                }});
	}
	service.soldImagesCountByUser = function(user){
		return $http.post('http://localhost:8080/Photoshop/rest/operator/numberSoldImagesFromUser', user);
	}
	service.averagePriceByAuthor = function(user){
		return $http.post('http://localhost:8080/Photoshop/rest/operator/averagePriceByAuthor', user);
	}
	service.frequentlyBoughtResByUser = function(user){
		return $http.post('http://localhost:8080/Photoshop/rest/operator/mostFrequentlyBoughtImageResolutionsByUser', user);
	}
	service.soldImagesCountByCategory = function(category){
		return $http.post('http://localhost:8080/Photoshop/rest/operator/numberSoldImagesFromCategory', category);
	}
	service.mostFrequentlyBoughtImageResolutionsInCategory = function(category){
		return $http.post('http://localhost:8080/Photoshop/rest/operator/mostFrequentlyBoughtImageResolutionsInCategory', category);
	}
	service.averagePriceInCategory = function(category){
		return $http.post('http://localhost:8080/Photoshop/rest/operator/averagePriceInCategory', category);
	}
	service.setAuthorCard = function(id){
		return $http.get('http://localhost:8080/Photoshop/rest/user/setAuthorCard/' + id);
	}
	service.changeUserPass = function(currPass, newPass){
		return $http.post('http://localhost:8080/Photoshop/rest/user/changePassword', "currentPassword=" + currPass
				+ "&newPassword=" + newPass, {headers: {
                	'Content-Type': 'application/x-www-form-urlencoded'
                }});
	}
	service.deleteAccount = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/user/deleteAccount');
	}
	service.registerFirm = function(firm){
		return $http.post('http://localhost:8080/Photoshop/rest/firm/register', firm);
	}
	service.getAppliedFirms = function(firm){
		return $http.get('http://localhost:8080/Photoshop/rest/operator/getAppliedFirms');
	}
	service.approveFirm = function(firm){
		return $http.post('http://localhost:8080/Photoshop/rest/operator/approveFirm', firm);
	}
	service.addFirmOperator = function(operator){
		return $http.post('http://localhost:8080/Photoshop/rest/operator/addFirmOperator', operator);
	}
	service.getFirmOperators = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/operator/getFirmOperators');
	}
	service.getAuthorFirmId = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/user/getAuthorFirmId');
	}
	service.getAllFirms = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/firm/getAll');
	}
	service.applyToFirm = function(firm){
		return $http.post('http://localhost:8080/Photoshop/rest/user/applyToFirm', firm);
	}
	service.checkIfAppliedToFirm = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/user/checkIfAppliedToFirm');
	}
	service.getAppliedAuthors = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/operator/getAppliedAuthors');
	}
	service.approveFirmAuthor = function(author){
		return $http.post('http://localhost:8080/Photoshop/rest/operator/approveFirmAuthor', author);
	}
	service.leaveFirm = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/user/leaveFirm');
	}
	service.getImagesFromUser = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/user/getImagesFromUser');
	}
	service.deleteImage = function(image){
		return $http.post('http://localhost:8080/Photoshop/rest/user/deleteImage', image);
	}
	service.getOwnedImages = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/user/getOwnedImages');
	}
	service.sendImage = function(image){
		return $http.post('http://localhost:8080/Photoshop/rest/user/sendImage', image);
	}
	
	
	return service;
}])
.factory('ImageService', ['$http', function($http){
	var service = {};
	
	service.getCategories = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/image/getCategories');
	}
	service.getResolutions = function(){
		return $http.get('http://localhost:8080/Photoshop/rest/image/getResolutions');
	}
	service.getSorted = function(init){
		return $http.get('http://localhost:8080/Photoshop/rest/image/sortedBy/'+init);
	}
	service.getSearch = function(test, init){
		if (test == 'Keywords')
			return $http.post('http://localhost:8080/Photoshop/rest/image/searchByKeyword', "keywords="+init.entry+"&init="+init.init+"&reverse="+init.reverse,
					{headers: {
						'Content-Type': 'application/x-www-form-urlencoded'
					}});
		else
			return $http.get('http://localhost:8080/Photoshop/rest/image/searchBy/'+init);
	}
	service.getImageThumbnail = function(image){
		return $http.post('http://localhost:8080/Photoshop/rest/image/getImageThumbnail', image);
	}
	service.getImagePreview = function(image){
		return $http.post('http://localhost:8080/Photoshop/rest/image/getImagePreview', image);
	}
	service.getById = function(id){
		return $http.get('http://localhost:8080/Photoshop/rest/image/' + id);
	}
	service.getCategoriesOfImage = function(id){
		return $http.get('http://localhost:8080/Photoshop/rest/image/getCategoriesOfImage/' + id);
	}
	service.getAvailableResolutions = function(id){
		return $http.get('http://localhost:8080/Photoshop/rest/image/getAvailableResolutions/' + id);
	}
	service.getResolutionsOfImage = function(id){
		return $http.get('http://localhost:8080/Photoshop/rest/image/getResolutionsOfImage/' + id);
	}
	service.getResolutionById = function(id){
		return $http.get('http://localhost:8080/Photoshop/rest/image/getResolutionById/' + id);
	}
	service.upload = function(data){
		var fd = new FormData();
		for (var key in data)
			fd.append(key, data[key]);
		
		for (var pair of fd.entries()) {
		    console.log(pair[0]+ ', ' + pair[1]); 
		}
		return $http.post('http://localhost:8080/Photoshop/rest/image/upload', fd, {
			transformRequest: angular.identity,
			headers: {'Content-Type': undefined}
		});
	}
	
	
	return service;
}])
.directive('validFile', ['$parse', function($parse){
  return {
    require:'ngModel',
    restrict: 'A',
    link:function(scope,el,attrs,ngModel){
    	var model = $parse(attrs.ngModel);
    	var modelSetter = model.assign;
      //change event is fired when file is selected
      el.bind('change',function(){
        scope.$apply(function(){
          ngModel.$setViewValue(el.val());
          ngModel.$render();
          modelSetter(scope, el[0].files[0]);
        });
      });
    }
  }
}]);