import React from "react";

export default class Header extends React.Component {
    render() {
        return (
            <nav class="navbar navbar-default navbar-inverse navbar-static-top" role="navigation">
                <div class="navbar-header">
                    <a class="navbar-brand" href="#">Core Admin</a>


                    <button type="button" class="navbar-toggle" data-toggle="collapse"
                            data-target=".navbar-collapse-primary">
                        <span class="sr-only">Toggle Side Navigation</span>
                        <i class="icon-th-list"></i>
                    </button>

                    <button type="button" class="navbar-toggle" data-toggle="collapse"
                            data-target=".navbar-collapse-top">
                        <span class="sr-only">Toggle Top Navigation</span>
                        <i class="icon-align-justify"></i>
                    </button>
                </div>


                <div class="collapse navbar-collapse navbar-collapse-top">
                    <div class="navbar-right">

                        <ul class="nav navbar-nav navbar-left">
                            <li class="cdrop active"><a href="#">Link</a></li>

                            <li class="cdrop"><a href="#">Link</a></li>

                            <li class="dropdown cdrop">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown <b
                                    class="caret"></b></a>
                                <ul class="dropdown-menu">
                                    <li><a href="#">Action</a></li>
                                    <li><a href="#">Another action</a></li>
                                    <li class="divider"></li>
                                    <li><a href="#">Separated link</a></li>
                                </ul>
                            </li>
                        </ul>

                        <form class="navbar-form navbar-left" role="search">
                            <div class="form-group">
                                <input type="text" class="search-query animated" placeholder="Search"></input>
                                <i class="icon-search"></i>

                            </div>
                        </form>

                        <ul class="nav navbar-nav navbar-left">
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle dropdown-avatar" data-toggle="dropdown">
                                  <span>
                                    <img class="menu-avatar"
                                         src="../../images/avatars/avatar1.jpg"/> <span>John Smith <i
                                      class="icon-caret-down"></i></span>
                                    <span class="badge badge-dark-red">5</span>
                                  </span>
                                </a>
                                <ul class="dropdown-menu">
                                    <li class="with-image">
                                        <div class="avatar">
                                            <img src="../../images/avatars/avatar1.jpg"/>
                                        </div>
                                        <span>John Smith</span>
                                    </li>

                                    <li class="divider"></li>

                                    <li><a href="#"><i class="icon-user"></i> <span>Profile</span></a></li>
                                    <li><a href="#"><i class="icon-cog"></i> <span>Settings</span></a></li>
                                    <li><a href="#"><i class="icon-envelope"></i> <span>Messages</span> <span
                                        class="label label-dark-red pull-right">5</span></a></li>
                                    <li><a href="#"><i class="icon-off"></i> <span>Logout</span></a></li>
                                </ul>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>
        )
    }
}