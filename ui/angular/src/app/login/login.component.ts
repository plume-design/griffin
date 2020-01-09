/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
import {Component, OnInit} from "@angular/core";
import {ServiceService} from "../service/service.service";
import {UserService} from "../service/user.service";
import {Router, ActivatedRoute} from "@angular/router";
import {LocationStrategy, HashLocationStrategy} from "@angular/common";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.css"],
  providers: [ServiceService, UserService]
})
export class LoginComponent implements OnInit {
  griffinUser: string;
  timestamp: Date;
  results: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    public serviceService: ServiceService,
    public userService: UserService
  ) {
  }

  loginBtnWait() {
    $("#login-btn")
      .addClass("disabled")
      .text("Logging in......");
  }

  loginBtnActive() {
    $("#login-btn")
      .removeClass("disabled")
      .text("Log in");
  }

  showLoginFailed() {
    $("#loginMsg")
      .show()
      .text("Login failed. Try again.");
  }

  // resizeMainWindow(){
  //     $('#mainWindow').height(window.innerHeight-50);
  // }

  submit(event) {
    if (event.which == 13) {
      //enter
      event.preventDefault();
      $("#login-btn").click();
      $("#login-btn").focus();
    }
  }

  focus($event) {
    $("#loginMsg").hide();
  }

  login() {
    var name = $("input:eq(0)").val();
    var password = $("input:eq(1)").val();
    this.loginBtnWait();
    this.http.post("login", {username: name, password: password}).subscribe(
      data => {
        this.results = data;
        if (this.results.status == 0) {
          this.userService.setCookie("griffinUser", this.results.griffinUser, 0);
          this.loginBtnActive();
          window.location.replace("/");
        } else {
          this.showLoginFailed();
          this.loginBtnActive();
        }
      },
      err => {
        this.showLoginFailed();
        this.loginBtnActive();
      }
    );
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      let login = params.get('login');
      if(login) {
        this.griffinUser = undefined;
        this.userService.setCookie("griffinUser", undefined, -1);
        window.location.reload();
      } else {
        this.griffinUser = this.userService.getCookie("griffinUser");
      }
    });
    this.timestamp = new Date();
  }
}
