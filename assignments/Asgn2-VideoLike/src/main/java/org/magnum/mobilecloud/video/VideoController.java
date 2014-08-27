/*
*
* Copyright 2014 Jules White
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
package org.magnum.mobilecloud.video;

import com.google.common.collect.Lists;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

@Controller
public class VideoController {
    /**
     * You will need to create one or more Spring controllers to fulfill the
     * requirements of the assignment. If you use this file, please rename it
     * to something other than "AnEmptyController"
     * <p/>
     * <p/>
     * ________ ________ ________ ________ ___ ___ ___ ________ ___ __
     * |\ ____\|\ __ \|\ __ \|\ ___ \ |\ \ |\ \|\ \|\ ____\|\ \|\ \
     * \ \ \___|\ \ \|\ \ \ \|\ \ \ \_|\ \ \ \ \ \ \ \\\ \ \ \___|\ \ \/ /|_
     * \ \ \ __\ \ \\\ \ \ \\\ \ \ \ \\ \ \ \ \ \ \ \\\ \ \ \ \ \ ___ \
     * \ \ \|\ \ \ \\\ \ \ \\\ \ \ \_\\ \ \ \ \____\ \ \\\ \ \ \____\ \ \\ \ \
     * \ \_______\ \_______\ \_______\ \_______\ \ \_______\ \_______\ \_______\ \__\\ \__\
     * \|_______|\|_______|\|_______|\|_______| \|_______|\|_______|\|_______|\|__| \|__|
     */

    @Autowired
    private VideoRepository videos;

    @RequestMapping(value = "/go", method = RequestMethod.GET)
    public
    @ResponseBody
    String goodLuck() {
        return "Good Luck!";
    }

    @RequestMapping(value = "/video", method = RequestMethod.POST)
    public
    @ResponseBody
    Video addVideo(@RequestBody Video v) {
        v.setLikes(0); //os likes começam a zero segundo o readme
        Video saved = videos.save(v);
        return v;
    }

    @RequestMapping(value = "/video", method = RequestMethod.GET)
    public
    @ResponseBody
    Collection<Video> getVideoList() {
        return Lists.newArrayList(videos.findAll());
    }


    @RequestMapping(value = "/video/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    Video getVideoById(@PathVariable("id") long id,
                       HttpServletResponse response) {
        Video v = videos.findOne(id);
        if (null == v) response.setStatus(404);
        return v;
    }

    @RequestMapping(value = "/video/search/findByName", method = RequestMethod.GET)
    public
    @ResponseBody
    Collection<Video> findByTitle(@RequestParam("title") String title) {
        return videos.findByName(title);
    }


    @RequestMapping(value = "/video/search/findByDurationLessThan", method = RequestMethod.GET)
    public
    @ResponseBody
    Collection<Video> findByDurationLessThan(@RequestParam("duration") long duration) {
        return videos.findByDurationLessThan(duration);
    }


    @RequestMapping(value = " /video/{id}/like", method = RequestMethod.POST)
    public void likeVideo(@PathVariable("id") long id,
                          HttpServletResponse response, Principal user) {
        Video v = videos.findOne(id);

        if (null == v) {
            response.setStatus(404);
            return;
        }

        if (v.containsLikeByUser(user.getName())) {
            response.setStatus(400);
            return;
        }

        v.addLikeByUser(user.getName());
        videos.save(v);
        response.setStatus(200);
    }

    @RequestMapping(value = " /video/{id}/unlike", method = RequestMethod.POST)
    public void unlikeVideo(@PathVariable("id") long id,
                            HttpServletResponse response, Principal user) {
        Video v = videos.findOne(id);

        if (null == v) {
            response.setStatus(404);
            return;
        }

        if (!v.containsLikeByUser(user.getName())) {
            response.setStatus(400);
            return;
        }

        v.removeLikeByUser(user.getName());
        videos.save(v);
        response.setStatus(200);
    }

    @RequestMapping(value = " /video/{id}/likedby ", method = RequestMethod.GET)
    public
    @ResponseBody
    Collection<String> getUsersWhoLikedVideo(@PathVariable("id") long id,
                                            HttpServletResponse response, Principal user) {
        Video v = videos.findOne(id);

        if (null == v) {
            response.setStatus(404);
            return new ArrayList<String>();
        }

        return new ArrayList<String>(v.getLikesUsernames());
    }
}
