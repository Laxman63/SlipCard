# Slip

## Inspiration
One day Eugene and Jeff went to a networking event for inter-bank socials. It was particularly awkward that when the other people handed them the business cards, none of the duo has a business card to give back. So that we came up with this idea ‘Slip’. On a second thought, we also realize that even if we have business cards, we would not be able to showcase our full profile, which are now, most of the time, hosted online. Hence we came up with the idea of a digital name card that not only has what a traditional name card has: personal description, email, phone numbers occupation, companies, but also a collection of his/her online portfolio such as GitHub, LinkedIn, Stack Overflow, and etc.   

## What it does
Slip is an digital information centralization hub that facilitates the exchange of information across a multitude of variant platforms for the purpose of enhancing peer to peer connection, or in short <b>"let's u add all accounts at once".</b>. 

### Login and sign up
Slip support login and sign up through email, as well as through Google and Facebook, so that the user might easily finishing his/her account set up very quickly and conveniently.

### Account Centralization
After you've register an account, slip would prompt you to integrate your online profiles. You may integrate GitHub, LinkedIn, and Stack Overflow as of right now. Once your account is integrated, they will show on your slipe profile as cards that has a brief overview of your say git repository, and when clicked, open up the detailed profile (such as going into github). 

### Scan to add
the app provides a convenient adding technique: it generates a short, encrypted hash which can be scanned on an other slip app to view the profile. When the scanner does not have slip, he can still scan sand save the code, and the information scanned will take him/her to the mobile site that stores the displayer's information. So that the information is still shared even when the scanner does not have slip. Then on the website, it will prompt the user who doesn't have slip to, hopefully, download slip. 

### Geo-location
Slip enables users to view potential connections that are potentially close to them (geographically, socially, and hierarchically), so that the users may use geo-location to buildup a network of nearby professionals.

### View Mutual Connection
If you have mutual connections in the first degree, and or second degree, you can view these connected connection as well. Then try to expand the connection from there. 

### Maintance
The user may easily maintain the platform by choosing which connections to add or remove by simply clicking add or the other remove button. When you remove some one, he/she doesn't get noticed, so it is not awkward at all. 

## How I built it
When we started building this project, we thought about building a fast, beautiful and maintainable app. That's why we choose to develop natively for Android, an ecosystem for approximately 70% of the singer

## Challenges I ran into
Integrating the app with firebase was the most challenging issue I've run into during the course of the project. In the past, I used to set up a mySQL server on local host. And then handle the request by having java jdec communicate in SQL directly with the database, or do it through PHP. however, since we are under a more strict time limit here at HackPrinceton (given 36 hours), I have no choice but to use a more established database connection service that is Firebase, provided by google, for Android. This database allowed authorization, google sign-in, cloud-messaging, and a Relational database all at once. However, since it is pretty high level, I do not have write the code for lower level functions, and so I was not quite sure how this structure works. In fact, different than previous dbs, at Google Firebase, you do not handle your own user session transition. The firebase handles it for you. So you make your changes to user log in state, and then through a listener called mAuthListener, the Firebase SDK decides whether your user data has changed and then make decisions for you, if user == null, then signs you up, if not != null then signs you in. Because I do not handle the user state transition myself. it was not that intuitive for me to catch that the firebase detects the user itself in a listener. This took me the entire Saturday afternoon to figure out. Enventually, by consulting my partner who is an expert in working with databases, I learned that the listeners are implemmented in each classes, so that when something goes out of hand, the listener would always catch that and take action against the response. 

## What I learned
I learnd that

## What's next for Slip
Next, we will continue use a (machine learning) 
