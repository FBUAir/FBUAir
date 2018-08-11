package me.gnahum12345.fbuair.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class FakeUsers {

    // placeholder users to add
    public JSONObject jsonUser1;
    // duplicate name
    public JSONObject jsonUser2;
    // duplicate number
    public JSONObject jsonUser3;
    // duplicate email
    public JSONObject jsonUser4;
    public JSONObject jsonUser5;
    // has +1 in number
    public JSONObject jsonUser6;
    // no last name
    public JSONObject jsonUser7;
    public JSONObject jsonUser8;

    public FakeUsers() {
        populateUsers();
    }

    private void populateUsers() {
        try {
            // populate fields
            jsonUser1 = new JSONObject()
                    .put("name", "Reese Jones")
                    .put("phoneNumber", "5478392306")
                    .put("email", "r2345@yahoo.com")
                    .put("organization", "Pinterest")
                    .put("uId", "23232")
                    .put("profileImage", "")
                    .put("socialMedias", "[]");

            jsonUser2 = new JSONObject()
                    .put("name", "Reese Amazing")
                    .put("uId", "13232")
                    .put("phoneNumber", "7482034937")
                    .put("email", "")
                    .put("organization", "Google")
                    .put("profileImage", "iVBORw0KGgoAAAANSUhEUgAAAVAAAAFQCAYAAADp6CbZAAABEGlDQ1BTa2lhAAAokYWRoU7EQBRF\n" +
                            "    DwuGhAQEYgViBAIDATYhCNQiGmxZkgXXdksh2XabaTfLD4BCYNDAT8Av4AkY+AgEQXO7Fa3pcidv\n" +
                            "    3snLy7y5M9BqI7VWIE5y6zpd0z89M9TkBVlKs+bg96PY4W1zRl+TFgdhFih/K3Kr4TpyIF6NSr4q\n" +
                            "    2C/5tmDbcw/Fj+KNqMZ+jSd5motfi/4gtQV/iQ/i4Tio7s1SmJwcK/cVa+yypTD0sHgkZJwTig0T\n" +
                            "    Lsm5EGW4OHRFjnpixqr/o+o9Rw+w/wPzd1XNv4eXG2h/VrX1J1i+huf31LPetLSgaHU65TvPmlH9\n" +
                            "    RYNXM/XqMNKKGMqh4UhuA7k38r/NDnt/SHVE+Bc2sDYAAAAEc0JJVAgICAh8CGSIAAAgAElEQVR4\n" +
                            "    nOy993McV57t+YH3BEDvRFKiKMOW6Zlu9cybtzHvxe7+2xsvNnbe9Jie7pZpOUoiJdF7AoR3+8PJ\n" +
                            "    o+9FqSozC6gCCqh7IjJgWACKmfee+7XnO0BGRj0MASPFdaK4ZpPrBDBffD5TXJPAaHGNNFyjDZ8P\n" +
                            "    N/m3weJv7wDrwEbF1fia9eJaARaLawF4AbxquBaKaw3YLK6MjFIMH/YbyDh0DAIDTT76c7/GBDpK\n" +
                            "    EOQ0MIWIchKYAMaKKyXI4SbXUHINJtdAi/c5wO735WsI2C6uYUS2jddW8Z7WgfHifW4gktwqXuO/\n" +
                            "    sZb8W/o7tlt83El+PqPPkAm0P5GS0GRyTRXXRHKNItIZJchxnCCi9PP0a5PoUBvXYMPXjWQ6iNbs\n" +
                            "    VnJtN3zdeG0iQlwrrlVgufi4UnxMr7Uml1+7Aiwl10rxuzYJQs3oI2QC7U+YQEeR630SOAWcLq75\n" +
                            "    4ppDVuYJdpNqM7JrJMHUotzr1fieh/i1dUmT7zWzHFuR7XbD5ylZvkZu/Svk9r8AnhbXM+A5YcFm\n" +
                            "    S7QP0cpdyjjaGCJiio0f7YbbUjxFEOgZmhOo45n+meOM1OJcojWBPkUE+oywaB0/dQx2M/mY46rH\n" +
                            "    EJlAjxdsuU0QxDdDJH18zRDJnqnkmkw+2h23256S8HGGic5JKLv2zVz45eLjIiJZJ6nSy8mr18Vr\n" +
                            "    s6V6jJBd+OMFu+ZjyHo8A5wFziXXeWRlnitekyZ0miWRUne6Hw5chx9G0UFSlkRyEmoBeISs0kdN\n" +
                            "    ruHitSvkWOmxQibQo4khojwoTf44XjmL3PL5Ftcs4Z6nRJnRPP7aDCmZugxrGt3bM8Al4CXh+j8v\n" +
                            "    PjoskFqyLrfKLv4RQ940RxNO/pxAFub55LpEkOM0u7PmY+x2y0eL31eXNDICaQIrzfI7hrqafL5E\n" +
                            "    JKQeAveKj77s6q8c3NvP6ASyBdrbaFbzOIisTWfNrwJvAteKz99GVpDJ8qDRyj3dafPzxq8H9vE5\n" +
                            "    Nb7fLtKwhg+kVlhHZLoE/Ah8B9xGB+AISkQ5lLKdXK4MgOz29ySy1dGbGCAK0U+wOzN+Mvl4suF7\n" +
                            "    84hYx4mC9YNGIyGmJUIpKTSWETX7mP6uxhKpZh9b/dtg8nsOY807VrqO3Phn7Hbrnzd8brf/CUpO\n" +
                            "    OaG13fiLMw4X2QLtTQwi8pxEhPgWcAVZmFeQqz5XXGkbpD/2SkzTZJiW8jSW92zw61KftBPIaOxo\n" +
                            "    alWi1fiaXqgaSAl9BB106f/1ZXE9RRbqT8XHgeLfV4hi/YweQibQw4fJ0kmIKVReZII8h0jzIopv\n" +
                            "    XkSk6qTR4K9/5b6RFoZvNlxbFV83XilBtnrtVovP0xZL17YON3yefm+kyb+lxJrWx5b9vqGGn29V\n" +
                            "    3F8XaVXDML8OrbiZ4SR6pvMoEXUGxUhNsE4+vSaSTlt7fE8ZHUAvWCn9jjEik/5GcV0mSo5MpK7n\n" +
                            "    nCFaLEfpzjNMu3eW0YZdSa7lho9pjWT6dVpYvl1y7bT4forGfvnBGpdJcJTdnVRprauvZl/7uYwk\n" +
                            "    f7MbcBZ+ld2iJxY6eYjKoe4BPxeXyXS1S+8powayBXr4GEab9CRwHfgYeB+561eLfzuMLLnd7xVk\n" +
                            "    /aSqRY1KRv7+c3YrG9n1PKzyHFuSY4Ri1CxRypV+r1FZahbdgzHCBe8WfBhOo3g3hAewjtz5H4Fv\n" +
                            "    ELm7nnSDTKCHikygBwsLckwgwnTv+dnisgV6EVmdtnw6gcayG1uIjVZkamXa+lxs8rpmX6cW6Bq7\n" +
                            "    s8iHAVvRG+i9bRPZ8GeUW59pN1cquDLR5LKYSuoRdOK5OXQxi9bEYPGeLiOL9ElxOeG0RJRSZRwA\n" +
                            "    sgt/sHChta3N9wnCvEjENaeIes2h4mf3+6zSuOYyYS1aFCO9nAV+SdQythsHTTPph1mCk8YeW8VO\n" +
                            "    W33tUjBrnabVD75OocNuHpHtfuOlRnrgua7UB9prRKD3gbvAD8BXwGPCA8g4AGQLtHuw22fxjlHC\n" +
                            "    6rwAvAf8FiWILiACNfay+dIN5zbDZpntTWRRPkHkaSvmcfL5M5QRfsmvNTOPGtJSqnZhMnUpmQVX\n" +
                            "    0uts8v1ZdlcGNCPruhZq+jpbv/PJvz9BBHoBEfdO8fEJenaOq26w9/9/RgUygXYHFvSYRKTpLqHG\n" +
                            "    nvRLaFNMd+jvOvmyRMQtbUm+JKwXd8UsNnzPl4Uy+l3n0uS7gizzNaLvfTq5nOBLv+dYq5OAFm9x\n" +
                            "    OdN+MYFIe6T4/Cy7++/d5fSUCK9kEu0wMoF2B4NoUc8jC/NmcV1GluZZIh7ayYJ3W5528dKs7X3C\n" +
                            "    OnGsbJ1yMeJ+Jk+I/79juq9oroGaxrYd177A7qoKH6rDdKZO1zHXObSmfoM8h/vAA+DL4nIMepVM\n" +
                            "    oB1HJtDOIFV3dyb3PNpEV1B75dvI8nRXUbtIicxdLZ734/jYMrIqnxIWyIPiekzENx3TzIXZ1UhD\n" +
                            "    ABstXuMY6wiy9NO48mN0kJ1HB6frfBtV/90QYTQj2PR7aZx2qviek1wOJUyi9fYArQXPgnJCrd8P\n" +
                            "    yH0jJ5H2D7ddjiLCvIkSRO4csmzcLLFhyvqmW6ExqeBET+quebNam9LZc2fHnaFN5/1kdAY+RJ38\n" +
                            "    S2tJPT9qFpHoRXYLwDhB5ZI12NvedC3pClFe9oTobPoBWaU/EQdwPkT3gWyB7h1pa54Ls68AHwIf\n" +
                            "    IYGPN9ltbdbdFCmxNRtDsUgUV99BwhR3kKv+A3LhswL6wcIWnePIKZyMmkDu/FtI/OVNgvD8881G\n" +
                            "    pBhV68cHuVW6QIfnbSLZ5APVce5eKDc7ssgEune4dOU00WZ5BbiB4l6n2ZulaXhRvyZq/RrLjZ4R\n" +
                            "    GfOnhHueN0RvwWGANfTchtBzfQB8S4xVSS+3c85SPbG0DMPF7/PU0kkUTrqPvJUnRGgnx0jbRCbQ\n" +
                            "    9uFFPIsI8zqRJLI7NkMkFvaCtBTpJWFh+npEZNFXCbk0u2VHuezoOCKth32OrL8HxBpxFv8Uskyv\n" +
                            "    Iet0CBHeMHsPt40gArUE4hV06DrJdKt43QI5Jto2MoHWgxME40Sh+3V0kt9ANZ3vIfJsVwEoVTZP\n" +
                            "    5+/YFbwPfM1uEn1KdPzkBX80kCajGtsv3ck0z+7W2FV0gLo0Kh3sl2oCNIMJd4iIvU8jEr1ArGn3\n" +
                            "    +48XfystYWtUxMpoQCfq0Y47BtACm0On9wfAH4C/Kz5/h6jn3EvftC3NDWRZ/oR6nj8D/gT8BVkK\n" +
                            "    PyCr5QVa4C6QzjgeSN38BWQlPkDrwR1Gq8Xrxtit8NQO/DNDyPJ1GOo8kb137HydTKClyBZoNVzT\n" +
                            "    6fbLT4rLivAnaB7wrwuLQqwjAv2muL4tPj4nSpUaFYwyjgfc7bWGwjJ3iXlXM+iQfrf4uI3WXKNQ\n" +
                            "    dB0MEB1Rl5El+hYRR/+0+LseLZKL7yuQCbQ10m6SN9BCexst5MtEB8pEG7/TyZ0NZGW4Q8glST8l\n" +
                            "    1z1kebwmW5v9gHT6J+weqzxGVF88QSVJTjzNIUK1uLYP82ZIS6TcZmxLdowILcwRFR3PiLBCRgOy\n" +
                            "    C98cA6gM5E2UHPp74J9Qt8cbaOG6ALodK8AF8MtogX4FfAH8Gfg34G9")
                    .put("socialMedias", "[]");

            jsonUser3 = new JSONObject()
                    .put("name", "Mary Smith")
                    .put("uId", "232132")
                    .put("phoneNumber", "5478392306")
                    .put("email", "mary@gmail.com")
                    .put("organization", "")
                    .put("profileImage", "")
                    .put("socialMedias", "[]");

            jsonUser4 = new JSONObject()
                    .put("name", "Ryan Smith")
                    .put("phoneNumber", "4958203748")
                    .put("email", "r2345@yahoo.com")
                    .put("uId", "233232")
                    .put("organization", "Airbnb")
                    .put("profileImage", "")
                    .put("socialMedias", "[]");

            jsonUser5 = new JSONObject()
                    .put("name", "Gaby Nahum")
                    .put("phoneNumber", "2039481726")
                    .put("email", "gabyn@yahoo.com")
                    .put("organization", "Facebook")
                    .put("uId", "232432")
                    .put("profileImage", "")
                    .put("socialMedias", "[]");

            jsonUser6 = new JSONObject()
                    .put("name", "Gaby Garcia")
                    .put("phoneNumber", "19283742837")
                    .put("email", "gabyg@gmail.com")
                    .put("uId", "263232")
                    .put("organization", "Facebook")
                    .put("profileImage", "")
                    .put("socialMedias", "[]");

            jsonUser7 = new JSONObject()
                    .put("name", "Mike")
                    .put("phoneNumber", "2938401927")
                    .put("email", "mike@fb.com")
                    .put("uId", "232382")
                    .put("organization", "Facebook")
                    .put("profileImage", "")
                    .put("socialMedias", "[]");

            jsonUser8 = new JSONObject()
                    .put("name", "Mariam Diallo")
                    .put("phoneNumber", "2938472633")
                    .put("email", "mariam@gmail.com")
                    .put("organization", "Facebook")
                    .put("uId", "203232")
                    .put("profileImage", "")
                    .put("socialMedias", "[]");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
