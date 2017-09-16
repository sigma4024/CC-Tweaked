package dan200.computercraft.core.apis.handles;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;

import javax.annotation.Nonnull;
import java.io.*;

import static dan200.computercraft.core.apis.ArgumentHelper.*;

public class EncodedInputHandle extends HandleGeneric
{
    private final BufferedReader m_reader;

    public EncodedInputHandle( BufferedReader reader )
    {
        super( reader );
        this.m_reader = reader;
    }

    public EncodedInputHandle( InputStream stream )
    {
        this( stream, "UTF-8" );
    }

    public EncodedInputHandle( InputStream stream, String encoding )
    {
        this( makeReader( stream, encoding ) );
    }

    private static BufferedReader makeReader( InputStream stream, String encoding )
    {
        if( encoding == null ) encoding = "UTF-8";
        InputStreamReader streamReader;
        try
        {
            streamReader = new InputStreamReader( stream, encoding );
        }
        catch( UnsupportedEncodingException e )
        {
            streamReader = new InputStreamReader( stream );
        }
        return new BufferedReader( streamReader );
    }

    @Nonnull
    @Override
    public String[] getMethodNames()
    {
        return new String[] {
            "readLine",
            "readAll",
            "close",
            "read",
        };
    }

    @Override
    public Object[] callMethod( @Nonnull ILuaContext context, int method, @Nonnull Object[] args ) throws LuaException
    {
        switch( method )
        {
            case 0:
                // readLine
                checkOpen();
                try
                {
                    String line = m_reader.readLine();
                    if( line != null )
                    {
                        return new Object[] { line };
                    }
                    else
                    {
                        return null;
                    }
                }
                catch( IOException e )
                {
                    return null;
                }
            case 1:
                // readAll
                checkOpen();
                try
                {
                    StringBuilder result = new StringBuilder( "" );
                    String line = m_reader.readLine();
                    while( line != null )
                    {
                        result.append( line );
                        line = m_reader.readLine();
                        if( line != null )
                        {
                            result.append( "\n" );
                        }
                    }
                    return new Object[] { result.toString() };
                }
                catch( IOException e )
                {
                    return null;
                }
            case 2:
                // close
                close();
                return null;
            case 3:
                // read
                checkOpen();
                try
                {
                    double count = optNumber( args, 0, 1 );
                    StringBuilder result = new StringBuilder( "" );
                    for ( int i = 1; i < count+1; i++ ) {
                        int character = m_reader.read();
                        if ( character == -1 ) {
                            if ( result.length() == 0 ) {
                                return null;
                            }
                            else {
                                return new Object[] { result.toString() };
                            }
                        }
                        char c = (char) character;
                        result.append( c );
                    }
                    return new Object[] { result.toString() };
                }
                catch( IOException e )
                {
                    return null;
                }
            default:
                return null;
        }
    }
}
