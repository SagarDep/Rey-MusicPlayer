/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.reyansh.audio.audioplayer.free.Lastfmapi.callbacks;


import com.reyansh.audio.audioplayer.free.Lastfmapi.models.LastfmAlbum;


import java.io.IOException;

public interface AlbuminfoListener {

    void albumInfoSucess(LastfmAlbum album) throws IOException;

    void albumInfoFailed();

}
