import { Directive, Input } from '@angular/core';
import { NG_VALIDATORS, ValidatorFn, AbstractControl, Validator } from '@angular/forms';

@Directive({
  selector: '[greaterThanZero]',
  providers: [{
      provide: NG_VALIDATORS,
      useExisting: ForbiddenAmountDirective,
      multi: true,
    }
  ]
})
export class ForbiddenAmountDirective implements Validator {
  validate(control: AbstractControl):  {[key: string]: any} | null {
    const forbidden = Number(control.value) <= 0;
    return forbidden ? {"forbiddenAmount": {value: control.value}} : null;
  }
}
