# MTP SDK Implementation Sample

This project showcases a minimal working implementation of an MTP SDK and pins attention to relevant implementation details. The provided solution is just one of many possible options. It is encouraged to come up with a solution that fits your business the best and use the sample app as a reference when needed.

Complete documentation is available at the [Developer portal](https://developer.meawallet.com/mtp/overview).

## Audience

The target audience is developers working on MTP SDK implementation. Access to the Meawallet Nexus repository for working with the MTP SDK test build is required.
<br><br>

# Getting Started

Find ```//TODO``` comments and perform required actions.

## Configure ```.gradle``` files

1. Configure access to the Meawallet Nexus repository in ```settings.gradle```. Fill in the repository group name and credentials.
2. In app level ```build.gradle``` file:
    - Set the correct ```applicationId```. The one you will be using in your app and which you sent to Meawallet to be built in your version of MTP SDK.
    - Set the desired release version of the MTP SDK. Use the latest release if possible.
    - Set the issuer name to your organization name. It will be provided by Meawallet.

## Provide configuration files for mobile services

Depending on your chosen PUSH service provider, replace placeholder files for ```google-services.json``` (for Google/Firebase) and ```agconnect-services.json``` (for HMS). <br>
If your company doesn't need support for HMS and hasn't ordered the MTP SDK build, you can remove all references to HMS in the code.

## Add signing config

Add your signing configuration (usually ```keystore.jks``` file). Make sure you have shared the certificate's fingerprint with Meawallet.
<br><br>

You are now set to lunch the sample app and digitize your first card.