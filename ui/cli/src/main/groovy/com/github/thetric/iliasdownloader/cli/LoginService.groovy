package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.service.IliasService

interface LoginService {
    IliasService connect()
}
