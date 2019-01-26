package lu.kremi151.brainfuck.util;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.widget.EditText;

import lu.kremi151.brainfuck.Brainfuck;
import lu.kremi151.brainfuck.R;
import lu.kremi151.brainfuck.adapters.AdapterCommandDescription;
import lu.kremi151.brainfuck.enums.EnumCellWidth;
import lu.kremi151.brainfuck.interfaces.IConsumer;

/**
 * Created by michm on 12.10.2016.
 */

public class DialogHelper {

    public static AlertDialog buildInputDialog(Context c, int titleRes, final IConsumer<String> callback){
        return buildInputDialog(c, titleRes, callback, null);
    }

    public static AlertDialog buildInputDialog(Context c, int titleRes, final IConsumer<String> callback, @Nullable  final Runnable cancelledCallback){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(titleRes);

// Set up the input
        final EditText input = new EditText(c);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.consume(input.getText().toString());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(cancelledCallback != null)cancelledCallback.run();
                dialog.cancel();
            }
        });

        return builder.create();
    }

    public static AlertDialog buildExamplesDialog(Context c, final IConsumer<Integer> listener){
        return buildItemsDialog(c, R.string.examples, R.array.examples_list, listener);
    }

    public static AlertDialog buildConversionLevelDialog(Context c, final IConsumer<Integer> listener){
        return buildItemsDialog(c, R.string.choose_conversion_lvl, R.array.conversion_level, listener);
    }

    public static AlertDialog buildInfoDialog(Context c, int titleRes, int contentRes){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(titleRes);
        builder.setMessage(contentRes);
        builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        return builder.create();
    }

    public static AlertDialog buildCommandsDialog(Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(R.string.commands);
        builder.setAdapter(new AdapterCommandDescription(c, (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE)), null);
        builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder.create();
    }

    public static AlertDialog buildSettigsDialog(Context c, final IConsumer<boolean[]> l){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(R.string.settings);
        Brainfuck bf = Brainfuck.getInstance();
        final boolean[] ba = new boolean[]{
                bf.isExtendedModeEnabled(),
                bf.sixteenBitModeEnabled(),
                bf.isInfiniteLoopDetectionEnabled(),
                bf.useBFKeyboardAsDefault()
        };
        builder.setMultiChoiceItems(R.array.settings_entries, ba, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                ba[i] = b;
            }
        });
        builder.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                l.consume(ba);
                dialogInterface.dismiss();
            }
        });
        return builder.create();
    }

    public static AlertDialog buildConfirmDialog(Context c, int titleRes, int messageRes, final Runnable onConfirmListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(titleRes);
        builder.setMessage(c.getString(messageRes));
        builder.setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onConfirmListener.run();
                dialogInterface.dismiss();
            }
        });
        return builder.create();
    }

    public static AlertDialog buildConfirmCodeChangementDialog(Context c, final String content, final IConsumer<String> callback){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(R.string.confirm_code_changements);
        builder.setMessage(content);
        builder.setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callback.consume(content);
                dialogInterface.dismiss();
            }
        });
        return builder.create();
    }

    public static AlertDialog buildIntervalDialog(Context c, final IConsumer<Long> callback){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(R.string.choose_interval);
        builder.setItems(new CharSequence[]{"25 ms", "50 ms", "100 ms", "250 ms", "500 ms", "1 s", "2 s", "5 s"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(i){
                    case 0:
                        callback.consume(25L);
                        break;
                    case 1:
                        callback.consume(50L);
                        break;
                    case 2:
                        callback.consume(100L);
                        break;
                    case 3:
                        callback.consume(250L);
                        break;
                    case 4:
                        callback.consume(500L);
                        break;
                    case 5:
                        callback.consume(1000L);
                        break;
                    case 6:
                        callback.consume(2000L);
                        break;
                    case 7:
                        callback.consume(5000L);
                        break;
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder.create();
    }

    private static AlertDialog.Builder preBuildMessageDialog(Context c, int titleRes, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(titleRes);
        builder.setMessage(message);
        builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder;
    }

    public static AlertDialog buildMessageDialog(Context c, int titleRes, String message){
        return preBuildMessageDialog(c, titleRes, message).create();
    }

    public static AlertDialog buildCopyableMessageDialog(Context c, int titleRes, final String message){
        AlertDialog.Builder builder = preBuildMessageDialog(c, titleRes, message);
        builder.setPositiveButton(android.R.string.copy, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClipData clip = ClipData.newPlainText("From Brainfuck converted C code", message);
                Brainfuck.getInstance().getClipboardManager().setPrimaryClip(clip);
                dialogInterface.dismiss();
            }
        });
        return builder.create();
    }

    public static AlertDialog buildItemsDialog(Context c, int titleRes, int itemsRes, final IConsumer<Integer> listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(titleRes);
        builder.setItems(itemsRes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.consume(i);
                dialogInterface.dismiss();
            }
        });
        builder.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        return builder.create();
    }

    public static AlertDialog buildItemsDialog(Context c, int titleRes, CharSequence items[], final IConsumer<Integer> listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(titleRes);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.consume(i);
                dialogInterface.dismiss();
            }
        });
        builder.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        return builder.create();
    }
}
