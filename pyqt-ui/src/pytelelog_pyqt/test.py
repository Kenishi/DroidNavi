#!/usr/bin/env python
#
# Copyright 2011 Google Inc.
# Modified by Jeremy May
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import logging
import webapp2
import urllib
import json
from google.appengine.api import urlfetch
from google.appengine.api import mail

recap_key = "6Lc_B_gSAAAAAIb_4XO0HS0Ll-dt0Rh8NGtXGDDV"

class MainHandler(webapp2.RequestHandler):

    RECAP_VERIFY_URL = "http://www.google.com/recaptcha/api/verify"

    def get(self):
        # Set the cross origin resource sharing header to allow AJAX
        self.response.headers.add_header('Access-Control-Allow-Origin', '*')
    
        # Print some JSON
        self.response.out.write('{"message":"Hello World!"}\n')

    def post(self):
        logging.debug("Start post")
        # Check type
        try:
            req_type = self.request.POST["type"]
        except KeyError:
            self.recap_fail("POST type absent in request.")
            return
            
        if req_type == "email": 
            self.process_recap()
        else:
            self.recap_fail("Unknown POST type.")
        pass
    
    def hasEmailParams(self):
        hasAll = True
        try:
            if not self.request.POST["c"]:
                hasAll = False
            if not self.request.POST["r"]:
                hasAll = False
            if not self.request.POST["name"]:
                hasAll = False
            if not self.request.POST["email"]:
                hasAll = False
            if not self.request.POST["message"]:
                hasAll = False
        except KeyError:
            hasAll = False
        return hasAll
    
    def process_recap(self):
        if not self.hasEmailParams():
            self.recap_fail("POST has missing parameters.")
            return
        
        # Get c (challenge)
        challenge = self.request.POST["c"];
        # Get r (response)
        response = self.request.POST["r"];
            
        # Get IP
        ip = self.request.remote_addr
        
        if self.isRecapParamsOk():
            # Build form fields
            form_fields = {
                "privatekey": recap_key,
                "remoteip": ip,
                "challenge": challenge,
                "response": response
            }
            
            # Send challenge/response to recap
            form_data = urllib.urlencode(form_fields)
            result = urlfetch.fetch(url=self.RECAP_VERIFY_URL,
                payload=form_data,
                method=urlfetch.POST,
                header={'Content-Type': 'application/x-www-form-urlencoded'})
                
            # Check status code
            if result.status_code == 200:
                # Check pass/fail and respond
                result_data = result.content.split('\n')
                if result_data[0] == "true":
                    self.recap_success()
                else:
                    self.recap_fail(result_data[1])
            else:
                self.recap_fail("Status Code: " + str(result.status_code))
        
        else:
            self.recap_fail("Paramters failed verify")
            
    def recap_success(self):
        # Get sender name
        name = self.request.POST["name"]
        
        # Get sender email
        sender_email = self.request.POST["email"]
        
        # Get message
        message = self.request.POST["message"]
        
        # Get IP
        ip = self.request.remote_addr
        
        # Build email
        email = mail.EmailMessage()
        email.sender = "Gallery Hangout <kenishi86+galleryhangout@gmail.com>"
        email.to = "Jeremy May <kenishi86+galleryhangout@gmail.com>"
        email.subject = "Gallery Hangout Contact"
        email.body = ("Name: " + name + "\n" +
            "Email: " + sender_email + "\n" +
            "IP: " + ip + 
            "Message: \n" + message)
        
        # Send to me
        email.send()
        
        # Send "OK" back to client
        pack = { "status": 200,
                "content": "OK" }
        json = json.dumps(pack)
        self.response.out.write(json)
        pass
    
    def recap_fail(self, msg):
        self.response.set_status(400)
        pack = { "status": 400,
                "content": msg }
        json = json.dumps(pack)
        self.response.out.write(json)
        pass
    
    def isRecapParamsOk(self, challenge, response, ip):
        if not challenge or len(challenge) <= 0:
            return False
        if not response or len(response) <= 0:
            return False
        if not ip or len(ip) <= 0:
            return False
        return True                   
        pass
    pass

app = webapp2.WSGIApplication([('/', MainHandler)],
                                         debug=True)

