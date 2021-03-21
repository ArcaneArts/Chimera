/*
 * This file is part of Chimera by Arcane Arts.
 *
 * Chimera by Arcane Arts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Chimera by Arcane Arts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License in this package for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Chimera.  If not, see <https://www.gnu.org/licenses/>.
 */

import 'package:application/chimera/functions.dart';
import 'package:hawkeye/chimera/protocol.dart';
import 'package:hawkeye/hawkeye.dart';
class INTERNALChimeraClientFunctionInvoker extends DefaultInvoker{
@override
dynamic invokeClientFunction(String func, List<dynamic> params){
print('ERROR: Unknown application function: $func');
return null;
}
@override
bool hasFunction(String func){
return false;}
}

