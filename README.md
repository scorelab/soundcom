# Soundcom
[![Gitter](https://badges.gitter.im/scorelab/soundcom.svg)](https://gitter.im/scorelab/soundcom?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

Soundcom is an open source android based app focussing on near field communication through soundwaves.

## Setup
Clone this repository to your local. Open the project in Android Studio. Download required sdk asked by gradle manager. Once gradle build is successfully finished you can install app on your Android device or test it on an emulator.

## Current Status
At present it works as a chat app with working range of few meters in air. It is likely to have more range under water but not tested yet.

## WorkFlow
App provides user options for both receiving and broadcasting message.

For broadcasting message, go to transmit window. Type the text you want to broadcast in the text area. Click on generate button, it creates a wav-file of the text data. Now a play button appears which plays the wav-file. Now your message is being broadcasted and is saved in the chat history.

At receiving end, click on mic button in receive window to record the broadcasted message. Recorded audio undergoes some processing and returns the broadcasted text which is then saved in the chat history.

To access chat history click on history button in navigation panel.

Video link - https://drive.google.com/file/d/1QSEJx1tP35t-_ZtvqYZ-KsA3ZNMTRbLB/view?usp=sharing

<div >
<img src="/images/Screenshot_1532763288.png" width=260px align=left>
<img src="/images/Screenshot_1532763181.png" width=260px align=left>
<img src="/images/Screenshot_1532763308.png" width=260px >
<img src="/images/Screenshot_1532763314.png" width=260px align=left style="margin-left:150px;">
<img src="/images/Screenshot_1532787514.png" width=260px >
</div>

## How to Contribute
Feel free to raise issues. As lot of testing has not been done on it so there are high chances of bugs.

You can make a Pull Request for any enhancement or issue.
