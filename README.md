# Photobase
Web platform for buying/selling photos. (<b>JAX-RS</b>, <b>Angular 1.2</b>, <b>MySQL</b>)

## Overview
Photobase provides an extensive list of <b>webshop</b> functionalities, with <b>multiple types of users</b>.<br>
It is a single page application (<b>SPA</b>) and it has both <b>front</b> end and <b>backend</b> data <b>validation</b>. The <b>Gmail API</b> is used for sending emails to users (account management, notifications, photo delivery etc).<br><br>
Website visitors can browse the gallery, and add photos to their <b>shopping cart</b>. All photos in the gallery have <b>watermarks</b>.
In order to complete a purchase an account is needed. After registration, account activation is required (with link via email - Gmail API).
Sold photos (in the desired resolutions) without the watermark are delivered to buyers, and the authors are notified, by email.
Users can apply to become authors and sell photos on the platform. They can also register firms.
Operators approve pending photos for sale, evaluate author applications and approve firm registrations.
Admins can create new categories for photos, add new operators, ban users, look up statistics etc.

## Functionalities
#### There are 5 types of users (roles):
* Unregistered users
* Buyers
* Authors
* Operators
* Admins

#### Functionalities available for all users:
* View photos and author profiles
* Search the gallery (by name, author name, category, keywords)
* Sort the gallery (by number of sales, date, price, name, rating)
* Add photos in desired resolutions to their shopping cart
* Register an account (account activation with link sent via Email is required - Gmail API)
* Login

#### Buyers and authors:
* Register credit card
* Buy photos (Images are delivered via email)
* Rate and comment owned photos
* Rate and comment authors (only if the user owns images from him)
* Apply to become an author by uploading 10 photos for evaluation
* Register Firm
* Change password
* Forgot password
* Delete account (account info is still stored in the database, it is just marked as deleted)
* Logout

#### Authors exclusive:
* Upload photos for sale (select categories, resolutions, add tags, specify prices etc)<br>
(a thumbnail and watermark are generated, and photos in the specified resolutions created)
* Apply to become a firm partner
* Receive notifications for sold images via email

#### Operators:
* Operators need to change their password when logging in for the first time in order to activate their account
* Approve pending images put up for sale
* Evaluate author applications
* Approve firm applications
* Ban users from the platform
* Inspect statistics for each image category. (Number of sold images, average price, most frequently bought resolutions etc)

#### Admins:
* Add new operators
* Remove operators
* Add new image categories

#### Firms:
* Receive notifications for author applications
* Receive notifications for every sale (when an author sells an image on behalf of the firm)

## Database schema
![Alt text](images/databaseSchema.png?raw=true "")

## Sidenote
This project was an assignment as part of the course - Web programming during the 6th semester at the Faculty of Computer Science in Belgrade. All platform functionalities were defined in the assignment specifications.

## Contributors
- Stefan Ginic - <stefangwars@gmail.com>
