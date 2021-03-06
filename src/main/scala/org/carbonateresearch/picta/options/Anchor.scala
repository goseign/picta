/* Author:          Fazal Khan
   Github:          acse-fk4517
   Github Repo:     https://github.com/acse-2019/irp-acse-fk4517
*/
package org.carbonateresearch.picta.options
/** Used to anchor the legend in place. */
sealed trait Anchor

/** Positions the legend to the left. */
case object LEFT extends Anchor

/** Positions the legend to the right. */
case object RIGHT extends Anchor

/** Positions the legend in the center. */
case object CENTER extends Anchor

/** Automatically positions the legend. */
case object AUTO extends Anchor