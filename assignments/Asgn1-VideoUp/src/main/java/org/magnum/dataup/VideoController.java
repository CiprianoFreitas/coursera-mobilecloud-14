/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.magnum.dataup;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.magnum.dataup.model.VideoStatus.VideoState;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class VideoController {

    public static final String VIDEO_SVC_PATH = "/video";
    private static final AtomicLong currentId = new AtomicLong(0L);

    private Map<Long, Video> videos = new HashMap<>();

    @RequestMapping(value = VIDEO_SVC_PATH, method = RequestMethod.GET)
    public
    @ResponseBody
    Collection<Video> getVideoList() {
        return videos.values();
    }

    @RequestMapping(value = VIDEO_SVC_PATH, method = RequestMethod.POST)
    public
    @ResponseBody
    Video addVideoData(@RequestBody Video v) {
        checkAndSetId(v);
        v.setDataUrl(getDataUrl(v.getId()));
        videos.put(v.getId(), v);
        return v;
    }

    @RequestMapping(value = "/video/{id}/data", method = RequestMethod.POST)
    public
    @ResponseBody
    VideoStatus addVideo(@PathVariable("id") long id,
                         @RequestParam("data") MultipartFile videoData,
                         HttpServletResponse response) throws IOException {

        if (videos.containsKey(id)) {
            VideoFileManager videoDataMgr = VideoFileManager.get();
            videoDataMgr.saveVideoData(videos.get(id),
                    videoData.getInputStream());
            return new VideoStatus(VideoState.READY);
        } else {
            response.setStatus(404);
            return null;
        }
    }

    @RequestMapping(value = "/video/{id}/data", method = RequestMethod.GET)
    public void getVideo(@PathVariable("id") long id,
                         HttpServletResponse response) throws IOException {
        if (videos.containsKey(id)) {
            VideoFileManager videoDataMgr = VideoFileManager.get();
            videoDataMgr.copyVideoData(videos.get(id),
                    response.getOutputStream());
            return;
        } else
            response.setStatus(404);
    }

    private void checkAndSetId(Video entity) {
        if (entity.getId() == 0) {
            entity.setId(currentId.incrementAndGet());
        }
    }

    private String getDataUrl(long videoId) {
        String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
        return url;
    }

    private String getUrlBaseForLocalServer() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        String base = "http://"
                + request.getServerName()
                + ((request.getServerPort() != 80) ? ":"
                + request.getServerPort() : "");
        return base;
    }
}
