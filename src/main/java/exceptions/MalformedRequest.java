/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author gamma
 */
public class MalformedRequest extends Exception{
    public MalformedRequest(String message) {
        super(message);
    }
    
}
