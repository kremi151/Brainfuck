package lu.kremi151.brainfuck.converters;

import lu.kremi151.brainfuck.directives.DirectiveBase;
import lu.kremi151.brainfuck.util.CharIterator;
import lu.kremi151.brainfuck.util.CodeHelper;
import lu.kremi151.brainfuck.util.ExecutionIterator;
import lu.kremi151.brainfuck.util.Optional;

/**
 * Created by michm on 09.06.2017.
 */

class GenuineConverter implements CodeConverter {
    @Override
    public String convert(String bfCode, boolean compressed) {
        StringBuilder csb = new StringBuilder(bfCode.length());
        CharIterator cci = new CharIterator(bfCode);
        while(cci.hasNext()){
            char n = cci.next();
            if(CodeHelper.isLegitBFCommand(n)){
                csb.append(n);
            }else if(n == ':' && cci.hasNext()){
                n = cci.next();
                Optional<DirectiveBase> odb = ExecutionIterator.findDirective(n);
                if(odb.isPresent()){
                    csb.append(odb.get().translate(cci));
                }else{
                    System.out.println("Unsupported directive: \"" + n + "\", ignoring");
                }
            }
        }
        return csb.toString();
    }
}
