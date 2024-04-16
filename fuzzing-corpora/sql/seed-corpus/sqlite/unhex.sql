
    SELECT hex( unhex( $hex ) );
  
    SELECT hex( unhex( lower( $hex ) ) );
  
  SELECT typeof( unhex('') ), length( unhex('') );

    SELECT unhex( $hex ) IS NULL;
  
    SELECT hex( unhex($hex, ' -') );
  
  SELECT typeof( unhex(' ', ' -') ), length( unhex('-', ' -') );
nullnullnull