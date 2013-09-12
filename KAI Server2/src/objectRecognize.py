import Image, ImageFilter, ImageChops, ImageEnhance, ImageStat
def objectRecognize( dstImage, refImage, boundbox ):
    # 
    source = refImage.crop(boundbox);
    dest = dstImage.crop(boundbox);

    # Smooth operation
    source = source.filter(ImageFilter.SMOOTH);
    dest = dest.filter(ImageFilter.SMOOTH);

    # Get difference between source image and reference image
    imdiff = ImageChops.difference(source, dest);

    # Increase the contrast of the difference image
    enhancer = ImageEnhance.Contrast(imdiff);
    imdiff = enhancer.enhance(1.1);
    imdiff = imdiff.convert('1');

    # Get bounding box of the object
    box = imdiff.getbbox();   # bound box of recognized object

    # Crop target object from the difference image
    recogObject = imdiff.crop(box);         

    # Get global image statics
    objStat = ImageStat.Stat(recogObject);
    tarStat = ImageStat.Stat(imdiff);

    # Get object region and object pixels occupied
    cropPixel = objStat.count[0];
    objectPixel = objStat.sum[0];
    objectPixel = int(objectPixel)/255;

    totalPixel = tarStat.count[0];
    objectPixel2 = tarStat.sum[0];
    objectPixel2 = int(objectPixel2)/255;

    # Caculate object pixels percent
    if float(cropPixel) > 0.0 and float(totalPixel) > 0.0:
        percent = float(objectPixel)/float(cropPixel);
        percent2 = float(objectPixel2)/float(totalPixel);
    else:
        percent = 0
        percent = 0
    
    print percent
    print percent2
	
    if percent >= 0.05 and percent2 >= 0.0005 :
        print "object detected\n"
        return True
    else:
        print "nothing detected\n"
        return False
        
    return percent;
