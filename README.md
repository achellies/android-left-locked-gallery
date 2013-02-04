Infinite Gallery Spinner
========================

Android gallery widget which has been modified to run continuously.

Instead of the standard gallery widget which slows down and eventually stops,
this gallery will run continuously until the user interacts with it again. It
can be stopped, but only manually.

Usage
=====

The infinite gallery is provided as a library project, so all you need to do is
reference the library project in your main android project.

In XML:

```xml
<com.scvngr.levelup.views.gallery.Gallery
    android:id="@+id/infinite_gallery"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/bg_gallery"
    android:padding="0dip" />
```

Use an adapter to supply the views for the gallery.

See the Example project in the sample/ directory for usage.

Modified By
===========

* Nate Roy (nateroy@thelevelup.com)
* Steve Pomeroy (spomeroy@mit.edu)

License
-------

Infinite Gallery is available under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
