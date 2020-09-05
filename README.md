# Photobase
Web platform for buying/selling photos. (JAX-RS, Angular 1.2, MySQL)

## Overview
Photobase provides an extensive list of webshop functionalities, with multiple types of users.<br>
Users can browse the galery and author profiles, and add photos to the shopping cart. After registration user account activation via email is required (Gmail API). Sold photos are delivered and the authors notified by email. Authors can sell each photo in multiple resolutions with different prices. After uploading a photo, it is resized accordingly, and a watermarked version and a thumbnail are created for the galery. Users can apply to become authors, and authors can apply to firms. Users can register firms.
Operators approve author applications and firms registrations, and look over statistics. Admins can create new categories for images, add new operators, ban users etc.
## Functionalities
#### There are 5 types of users (roles):
* Unregistered users
* Buyers
* Authors
* Operators
* Admins

#### Functionalities available for all users:
* View photos and author profiles
* Search galery (by name, author name, category, keywords)
* Sort galery (by number of sales, date, price, name, rating)
* Add photos in desired resolutions to shopping cart
* Register (account activation with link sent via Email is required - Gmail API)
* Login

#### Buyers and authors:
* Register credit card
* Buy, rate and comment owned photos (Images are delivered via email)
* Rate and comment authors (only if the user bought from that author)
* Apply to become an author by uploading 10 photos for evaluation
* Register Firm
* Change password
* Forgot password
* Delete account (account info is still stored in the database, it is just marked as deleted)
* Logout

#### Authors exclusive:
* Upload photos (select categories, resolutions, add tags, specify prices etc)
* Apply to firm
* Recieve notifications for sold imagesvia email

#### Operators:
* Operators need to change their password when logging in for the first time in order to activate their account
* Approve pending images put up for sale
* Approve author applications
* Appprove firm applications
* Ban users from the platform
* Inspect statistics for each image category. (Number of sold images, average price, most frequently bought resolutions etc)

#### Admins:
* Add new operators
* Remove operators
* Add new image categories

## Sidenote
This project was an assignment as part of the course - Web programming in the 6th semester at the Faculty of Computer Science in Belgrade. All platform functionalities were defined in the assignment specifications.

## Contributors
- Stefan Ginic - <stefangwars@gmail.com>
